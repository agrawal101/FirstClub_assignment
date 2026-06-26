package com.firstclub.membership.dto;

import com.firstclub.membership.entity.TierName;

import java.util.List;

public record TierResponse(Long id, TierName name, int level, List<BenefitResponse> benefits) {
}
