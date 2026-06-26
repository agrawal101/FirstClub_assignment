package com.firstclub.membership.service;

import com.firstclub.membership.dto.OrderRecordedResponse;
import com.firstclub.membership.entity.BillingCycle;
import com.firstclub.membership.entity.Cohort;
import com.firstclub.membership.entity.MembershipActivity;
import com.firstclub.membership.entity.Plan;
import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.entity.SubscriptionStatus;
import com.firstclub.membership.entity.Tier;
import com.firstclub.membership.entity.TierName;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.repository.MembershipActivityRepository;
import com.firstclub.membership.repository.SubscriptionRepository;
import com.firstclub.membership.service.tier.TierEvaluator;
import com.firstclub.membership.support.TestEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MembershipActivityServiceTest {

    private final MembershipActivityRepository activityRepository = mock(MembershipActivityRepository.class);
    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private final TierEvaluator tierEvaluator = mock(TierEvaluator.class);
    private final UserService userService = mock(UserService.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-03-01T00:00:00Z"), ZoneOffset.UTC);

    private final User user = new User("Ann", "ann@example.com", Cohort.REGULAR);
    private final Plan monthly = new Plan(BillingCycle.MONTHLY, new BigDecimal("299.00"));
    private final Tier silver = TestEntities.tier(1L, TierName.SILVER, 1, TestEntities.criteria(0, "0", null));
    private final Tier gold = TestEntities.tier(2L, TierName.GOLD, 2, TestEntities.criteria(5, "5000", null));

    private MembershipActivityService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(user, "id", 1L);
        service = new MembershipActivityService(
                activityRepository, subscriptionRepository, tierEvaluator, userService, clock);
    }

    @Test
    void recordingOrderAutoUpgradesActiveSubscription() {
        MembershipActivity activity = new MembershipActivity(user, LocalDate.of(2026, 3, 1));
        Subscription subscription = Subscription.start(user, monthly, silver, LocalDate.of(2026, 3, 1));
        when(userService.getEntity(1L)).thenReturn(user);
        when(activityRepository.findByUserId(1L)).thenReturn(Optional.of(activity));
        when(tierEvaluator.evaluate(any(), any())).thenReturn(gold);
        when(subscriptionRepository.findByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));

        OrderRecordedResponse response = service.recordOrder(1L, new BigDecimal("6000"));

        assertThat(subscription.getTier()).isEqualTo(gold);
        assertThat(response.currentTier()).isEqualTo(TierName.GOLD);
        assertThat(response.orderCount()).isEqualTo(1);
    }

    @Test
    void recordingOrderNeverDowngradesMidWindow() {
        MembershipActivity activity = new MembershipActivity(user, LocalDate.of(2026, 3, 1));
        Subscription subscription = Subscription.start(user, monthly, gold, LocalDate.of(2026, 3, 1));
        when(userService.getEntity(1L)).thenReturn(user);
        when(activityRepository.findByUserId(1L)).thenReturn(Optional.of(activity));
        when(tierEvaluator.evaluate(any(), any())).thenReturn(silver);
        when(subscriptionRepository.findByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));

        OrderRecordedResponse response = service.recordOrder(1L, new BigDecimal("100"));

        assertThat(subscription.getTier()).isEqualTo(gold);
        assertThat(response.currentTier()).isEqualTo(TierName.GOLD);
    }

    @Test
    void windowResetReevaluatesAndDowngradesWhenSpendDropsOff() {
        MembershipActivity activity = new MembershipActivity(user, LocalDate.of(2026, 1, 1));
        activity.recordOrder(new BigDecimal("9000"));
        Subscription subscription = Subscription.start(user, monthly, gold, LocalDate.of(2026, 1, 1));
        when(activityRepository.findByWindowStartBefore(any())).thenReturn(List.of(activity));
        when(tierEvaluator.evaluate(any(), any())).thenReturn(silver);
        when(subscriptionRepository.findByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));

        int reset = service.resetMonthlyWindows();

        assertThat(reset).isEqualTo(1);
        assertThat(activity.getMonthlySpend()).isEqualByComparingTo("0");
        assertThat(subscription.getTier()).isEqualTo(silver);
    }
}
