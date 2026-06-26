package com.firstclub.membership.dto;

import com.firstclub.membership.entity.BillingCycle;

import java.math.BigDecimal;

public record PlanResponse(Long id, BillingCycle billingCycle, int durationMonths, BigDecimal price) {
}
