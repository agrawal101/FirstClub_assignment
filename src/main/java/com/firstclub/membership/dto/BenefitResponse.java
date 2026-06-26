package com.firstclub.membership.dto;

import com.firstclub.membership.entity.BenefitType;

public record BenefitResponse(BenefitType type, String value) {
}
