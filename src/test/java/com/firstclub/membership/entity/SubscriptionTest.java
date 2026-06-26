package com.firstclub.membership.entity;

import com.firstclub.membership.exception.BusinessRuleException;
import com.firstclub.membership.support.TestEntities;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubscriptionTest {

    private final User user = new User("Ann", "ann@example.com", Cohort.REGULAR);
    private final Plan quarterly = new Plan(BillingCycle.QUARTERLY, new BigDecimal("799.00"));
    private final Tier silver = TestEntities.tier(1L, TierName.SILVER, 1,
            TestEntities.criteria(0, "0", null));
    private final Tier gold = TestEntities.tier(2L, TierName.GOLD, 2,
            TestEntities.criteria(5, "5000", null));

    @Test
    void startComputesExpiryFromBillingCycle() {
        LocalDate today = LocalDate.of(2026, 1, 10);

        Subscription subscription = Subscription.start(user, quarterly, silver, today);

        assertThat(subscription.isActive()).isTrue();
        assertThat(subscription.getStartDate()).isEqualTo(today);
        assertThat(subscription.getEndDate()).isEqualTo(LocalDate.of(2026, 4, 10));
    }

    @Test
    void changeTierReplacesTierWhileActive() {
        Subscription subscription = Subscription.start(user, quarterly, silver, LocalDate.now());

        subscription.changeTier(gold);

        assertThat(subscription.getTier()).isEqualTo(gold);
    }

    @Test
    void cancelledSubscriptionCannotChangeTier() {
        Subscription subscription = Subscription.start(user, quarterly, silver, LocalDate.now());
        subscription.cancel();

        assertThatThrownBy(() -> subscription.changeTier(gold))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("CANCELLED");
    }

    @Test
    void expiredSubscriptionCannotBeCancelled() {
        Subscription subscription = Subscription.start(user, quarterly, silver, LocalDate.now());
        subscription.expire();

        assertThatThrownBy(subscription::cancel)
                .isInstanceOf(BusinessRuleException.class);
    }
}
