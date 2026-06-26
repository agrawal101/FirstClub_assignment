package com.firstclub.membership.dto;

import com.firstclub.membership.entity.TierName;

import java.math.BigDecimal;

/**
 * Result of recording an order: the updated activity totals and the tier the user now qualifies for.
 */
public record OrderRecordedResponse(
        int orderCount,
        BigDecimal monthlySpend,
        BigDecimal totalSpend,
        TierName currentTier) {
}
