package com.firstclub.membership.dto;

import com.firstclub.membership.entity.TierName;
import jakarta.validation.constraints.NotNull;

public record ChangeTierRequest(@NotNull TierName tier) {
}
