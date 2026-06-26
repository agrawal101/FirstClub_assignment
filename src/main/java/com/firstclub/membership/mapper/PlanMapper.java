package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.PlanResponse;
import com.firstclub.membership.entity.Plan;

public final class PlanMapper {

    private PlanMapper() {
    }

    public static PlanResponse toResponse(Plan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getBillingCycle(),
                plan.getBillingCycle().months(),
                plan.getPrice());
    }
}
