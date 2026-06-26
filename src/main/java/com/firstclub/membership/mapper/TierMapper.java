package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.BenefitResponse;
import com.firstclub.membership.dto.TierResponse;
import com.firstclub.membership.entity.Tier;

import java.util.List;

public final class TierMapper {

    private TierMapper() {
    }

    public static TierResponse toResponse(Tier tier) {
        List<BenefitResponse> benefits = tier.getBenefits().stream()
                .map(b -> new BenefitResponse(b.getType(), b.getValue()))
                .toList();
        return new TierResponse(tier.getId(), tier.getName(), tier.getLevel(), benefits);
    }
}
