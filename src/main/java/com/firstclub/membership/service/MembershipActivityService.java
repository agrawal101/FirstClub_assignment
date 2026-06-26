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

        LocalDate today = LocalDate.now(clock);
        if (activity.getWindowStart().isBefore(today.minusMonths(1))) {
            activity.resetMonthlyWindow(today);
        }
        activity.recordOrder(amount);
        Tier qualified = tierEvaluator.evaluate(user, activity);
        TierName currentTier = applyUpgradeIfQualified(userId, qualified);

        log.info("Order recorded for user {}: orders={}, monthlySpend={}, tier={}",
                userId, activity.getOrderCount(), activity.getMonthlySpend(), currentTier);
        return new OrderRecordedResponse(
                activity.getOrderCount(), activity.getMonthlySpend(), activity.getTotalSpend(), currentTier);
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
