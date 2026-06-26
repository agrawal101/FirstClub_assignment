package com.firstclub.membership.dto;

import com.firstclub.membership.entity.BillingCycle;
import com.firstclub.membership.entity.TierName;
import jakarta.validation.constraints.NotNull;

public record SubscribeRequest(
        @NotNull Long userId,
        @NotNull BillingCycle billingCycle,
        @NotNull TierName tier) {
}
