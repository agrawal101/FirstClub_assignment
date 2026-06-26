package com.firstclub.membership.service;

import com.firstclub.membership.dto.OrderRecordedResponse;
import com.firstclub.membership.entity.MembershipActivity;
import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.entity.SubscriptionStatus;
import com.firstclub.membership.entity.Tier;
import com.firstclub.membership.entity.TierName;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipActivityRepository;
import com.firstclub.membership.repository.SubscriptionRepository;
import com.firstclub.membership.service.tier.TierEvaluator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Records member purchasing activity and drives automatic tier progression. Recording an order
 * only ever raises a member's tier, so a member is never demoted automatically; downgrades are an
 * explicit user action. The monthly spend window is rolled over lazily when a member's first order
 * of a new month arrives.
 */
@Service
@RequiredArgsConstructor
public class MembershipActivityService {

    private static final Logger log = LoggerFactory.getLogger(MembershipActivityService.class);

    private final MembershipActivityRepository activityRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TierEvaluator tierEvaluator;
    private final UserService userService;
    private final Clock clock;

    @Transactional
    public OrderRecordedResponse recordOrder(Long userId, BigDecimal amount) {
        User user = userService.getEntity(userId);
        MembershipActivity activity = activityRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No membership activity for user: " + userId));

        activity.recordOrder(amount);
        Tier qualified = tierEvaluator.evaluate(user, activity);
        TierName currentTier = applyUpgradeIfQualified(userId, qualified);

        log.info("Order recorded for user {}: orders={}, monthlySpend={}, tier={}",
                userId, activity.getOrderCount(), activity.getMonthlySpend(), currentTier);
        return new OrderRecordedResponse(
                activity.getOrderCount(), activity.getMonthlySpend(), activity.getTotalSpend(), currentTier);
    }

    /**
     * Opens a fresh monthly spend window for members whose window is over a month old, then
     * re-evaluates their tier. Because spend resets to zero, this is the path where a member can
     * be downgraded. Invoked by the monthly window-reset scheduler; returns the number reset.
     */
    @Transactional
    public int resetMonthlyWindows() {
        LocalDate today = LocalDate.now(clock);
        List<MembershipActivity> due = activityRepository.findByWindowStartBefore(today.minusMonths(1));
        for (MembershipActivity activity : due) {
            activity.resetMonthlyWindow(today);
            Tier qualified = tierEvaluator.evaluate(activity.getUser(), activity);
            applyQualifiedTier(activity.getUser().getId(), qualified);
        }
        if (!due.isEmpty()) {
            log.info("Reset monthly window for {} member(s)", due.size());
        }
        return due.size();
    }

    /** Sets the active subscription to the qualified tier in either direction (upgrade or downgrade). */
    private void applyQualifiedTier(Long userId, Tier qualified) {
        subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .ifPresent(subscription -> {
                    if (subscription.getTier().getLevel() != qualified.getLevel()) {
                        log.info("Re-evaluated subscription {}: {} -> {}",
                                subscription.getId(), subscription.getTier().getName(), qualified.getName());
                        subscription.changeTier(qualified);
                    }
                });
    }

    /**
     * Upgrades the user's active subscription if they now qualify for a higher tier.
     * Returns the tier the user effectively holds (their subscription tier, or the qualified
     * tier when they have no active subscription yet).
     */
    private TierName applyUpgradeIfQualified(Long userId, Tier qualified) {
        Optional<Subscription> active =
                subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
        if (active.isEmpty()) {
            return qualified.getName();
        }
        Subscription subscription = active.get();
        if (qualified.isHigherThan(subscription.getTier())) {
            log.info("Auto-upgrading subscription {}: {} -> {}",
                    subscription.getId(), subscription.getTier().getName(), qualified.getName());
            subscription.changeTier(qualified);
        }
        return subscription.getTier().getName();
    }
}
