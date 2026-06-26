package com.firstclub.membership.service;

import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.entity.BillingCycle;
import com.firstclub.membership.entity.Cohort;
import com.firstclub.membership.entity.Plan;
import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.entity.SubscriptionStatus;
import com.firstclub.membership.entity.Tier;
import com.firstclub.membership.entity.TierName;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.exception.BusinessRuleException;
import com.firstclub.membership.repository.PlanRepository;
import com.firstclub.membership.repository.SubscriptionRepository;
import com.firstclub.membership.repository.TierRepository;
import com.firstclub.membership.support.TestEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubscriptionServiceTest {

    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private final PlanRepository planRepository = mock(PlanRepository.class);
    private final TierRepository tierRepository = mock(TierRepository.class);
    private final UserService userService = mock(UserService.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-01-10T00:00:00Z"), ZoneOffset.UTC);

    private final User user = new User("Ann", "ann@example.com", Cohort.REGULAR);
    private final Plan monthly = new Plan(BillingCycle.MONTHLY, new BigDecimal("299.00"));
    private final Tier silver = TestEntities.tier(1L, TierName.SILVER, 1, TestEntities.criteria(0, "0", null));
    private final Tier gold = TestEntities.tier(2L, TierName.GOLD, 2, TestEntities.criteria(5, "5000", null));

    private SubscriptionService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(user, "id", 1L);
        service = new SubscriptionService(
                subscriptionRepository, planRepository, tierRepository, userService, clock);
    }

    @Test
    void subscribeCreatesActiveSubscriptionWithExpiryFromPlan() {
        when(userService.getEntity(1L)).thenReturn(user);
        when(subscriptionRepository.existsByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE)).thenReturn(false);
        when(planRepository.findByBillingCycle(BillingCycle.MONTHLY)).thenReturn(Optional.of(monthly));
        when(tierRepository.findByName(TierName.SILVER)).thenReturn(Optional.of(silver));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));

        SubscriptionResponse response =
                service.subscribe(new SubscribeRequest(1L, BillingCycle.MONTHLY, TierName.SILVER));

        assertThat(response.status()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(response.tier()).isEqualTo(TierName.SILVER);
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2026, 2, 10));
        assertThat(response.daysRemaining()).isEqualTo(31);
    }

    @Test
    void subscribeRejectedWhenUserAlreadyHasActiveSubscription() {
        when(userService.getEntity(1L)).thenReturn(user);
        when(subscriptionRepository.existsByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE)).thenReturn(true);

        assertThatThrownBy(() -> service.subscribe(new SubscribeRequest(1L, BillingCycle.MONTHLY, TierName.SILVER)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already has an active subscription");
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void changeTierMovesActiveSubscriptionToTargetTier() {
        Subscription subscription = Subscription.start(user, monthly, silver, LocalDate.of(2026, 1, 1));
        when(subscriptionRepository.findById(10L)).thenReturn(Optional.of(subscription));
        when(tierRepository.findByName(TierName.GOLD)).thenReturn(Optional.of(gold));

        SubscriptionResponse response = service.changeTier(10L, TierName.GOLD);

        assertThat(response.tier()).isEqualTo(TierName.GOLD);
        assertThat(subscription.getTier()).isEqualTo(gold);
    }

    @Test
    void expireOverdueExpiresOnlyReturnedSubscriptions() {
        Subscription overdue = Subscription.start(user, monthly, silver, LocalDate.of(2025, 1, 1));
        when(subscriptionRepository.findByStatusAndEndDateBefore(eq(SubscriptionStatus.ACTIVE), any()))
                .thenReturn(java.util.List.of(overdue));

        int expired = service.expireOverdue();

        assertThat(expired).isEqualTo(1);
        assertThat(overdue.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
    }
}
