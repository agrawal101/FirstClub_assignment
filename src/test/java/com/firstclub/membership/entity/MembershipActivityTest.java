package com.firstclub.membership.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipActivityTest {

    private final User user = new User("Ann", "ann@example.com", Cohort.REGULAR);

    @Test
    void recordOrderAccumulatesMonthlyAndLifetimeSpend() {
        MembershipActivity activity = new MembershipActivity(user, LocalDate.of(2026, 1, 1));

        activity.recordOrder(new BigDecimal("1000"));
        activity.recordOrder(new BigDecimal("500"));

        assertThat(activity.getOrderCount()).isEqualTo(2);
        assertThat(activity.getMonthlySpend()).isEqualByComparingTo("1500");
        assertThat(activity.getTotalSpend()).isEqualByComparingTo("1500");
    }

    @Test
    void resetMonthlyWindowClearsMonthlySpendButKeepsLifetimeTotal() {
        MembershipActivity activity = new MembershipActivity(user, LocalDate.of(2026, 1, 1));
        activity.recordOrder(new BigDecimal("2000"));

        activity.resetMonthlyWindow(LocalDate.of(2026, 2, 1));

        assertThat(activity.getMonthlySpend()).isEqualByComparingTo("0");
        assertThat(activity.getTotalSpend()).isEqualByComparingTo("2000");
        assertThat(activity.getWindowStart()).isEqualTo(LocalDate.of(2026, 2, 1));
    }
}
