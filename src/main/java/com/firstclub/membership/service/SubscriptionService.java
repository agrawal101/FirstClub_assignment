package com.firstclub.membership.service;

import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.entity.BillingCycle;
import com.firstclub.membership.entity.Plan;
import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.entity.SubscriptionStatus;
import com.firstclub.membership.entity.Tier;
import com.firstclub.membership.entity.TierName;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.exception.BusinessRuleException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.mapper.SubscriptionMapper;
import com.firstclub.membership.repository.PlanRepository;
import com.firstclub.membership.repository.SubscriptionRepository;
import com.firstclub.membership.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Owns the subscription lifecycle: subscribe, change tier (upgrade/downgrade), cancel, and
 * read the current membership. Lifecycle rules live on the {@link Subscription} entity; this
 * service orchestrates lookups, the one-active-subscription rule, and logging.
 */
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final TierRepository tierRepository;
    private final UserService userService;
    private final Clock clock;

    @Transactional
    public SubscriptionResponse subscribe(SubscribeRequest request) {
        User user = userService.getEntity(request.userId());
        LocalDate today = LocalDate.now(clock);
        requireNoLiveSubscription(user.getId(), today);

        Plan plan = findPlan(request.billingCycle());
        Tier tier = findTier(request.tier());

        Subscription subscription = subscriptionRepository.save(Subscription.start(user, plan, tier, today));
        log.info("User {} subscribed: plan={}, tier={}", user.getId(), plan.getBillingCycle(), tier.getName());
        return SubscriptionMapper.toResponse(subscription, today);
    }

    @Transactional
    public SubscriptionResponse changeTier(Long subscriptionId, TierName targetTier) {
        Subscription subscription = findSubscription(subscriptionId);
        Tier current = subscription.getTier();
        Tier next = findTier(targetTier);

        subscription.changeTier(next);
        String direction = next.isHigherThan(current) ? "UPGRADE" : "DOWNGRADE";
        log.info("Subscription {} {}: {} -> {}", subscriptionId, direction, current.getName(), next.getName());
        return SubscriptionMapper.toResponse(subscription, LocalDate.now(clock));
    }

    @Transactional
    public SubscriptionResponse cancel(Long subscriptionId) {
        Subscription subscription = findSubscription(subscriptionId);
        subscription.cancel();
        log.info("Subscription {} cancelled", subscriptionId);
        return SubscriptionMapper.toResponse(subscription, LocalDate.now(clock));
    }

    @Transactional
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        LocalDate today = LocalDate.now(clock);
        Subscription subscription = subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .filter(s -> !expireIfEnded(s, today))
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription for user: " + userId));
        return SubscriptionMapper.toResponse(subscription, today);
    }

    /**
     * Rejects a new subscription if the user already holds a live (active and not-yet-ended) one.
     * An active subscription whose end date has passed is expired on the spot so it no longer blocks.
     */
    private void requireNoLiveSubscription(Long userId, LocalDate today) {
        boolean live = subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .filter(s -> !expireIfEnded(s, today))
                .isPresent();
        if (live) {
            throw new BusinessRuleException("User already has an active subscription");
        }
    }

    /** Expires the subscription if its end date has passed. Returns true if it was expired. */
    private boolean expireIfEnded(Subscription subscription, LocalDate today) {
        if (subscription.hasEnded(today)) {
            subscription.expire();
            log.info("Subscription {} expired (end date {})", subscription.getId(), subscription.getEndDate());
            return true;
        }
        return false;
    }

    private Subscription findSubscription(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
    }

    private Plan findPlan(BillingCycle billingCycle) {
        return planRepository.findByBillingCycle(billingCycle)
                .orElseThrow(() -> new ResourceNotFoundException("No plan for billing cycle: " + billingCycle));
    }

    private Tier findTier(TierName name) {
        return tierRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found: " + name));
    }
}
