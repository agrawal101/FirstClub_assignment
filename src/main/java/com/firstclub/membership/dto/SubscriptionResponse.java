package com.firstclub.membership.dto;

import com.firstclub.membership.entity.BillingCycle;
import com.firstclub.membership.entity.SubscriptionStatus;
import com.firstclub.membership.entity.TierName;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SubscriptionResponse(
        Long id,
        Long userId,
        BillingCycle billingCycle,
        BigDecimal price,
        TierName tier,
        SubscriptionStatus status,
        LocalDate startDate,
        LocalDate endDate,
        long daysRemaining) {
}
