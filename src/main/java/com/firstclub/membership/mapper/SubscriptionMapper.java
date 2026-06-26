package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.entity.Subscription;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class SubscriptionMapper {

    private SubscriptionMapper() {
    }

    public static SubscriptionResponse toResponse(Subscription subscription, LocalDate today) {
        long daysRemaining = Math.max(0, ChronoUnit.DAYS.between(today, subscription.getEndDate()));
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getUser().getId(),
                subscription.getPlan().getBillingCycle(),
                subscription.getPlan().getPrice(),
                subscription.getTier().getName(),
                subscription.getStatus(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                daysRemaining);
    }
}
