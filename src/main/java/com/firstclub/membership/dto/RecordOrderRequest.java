package com.firstclub.membership.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RecordOrderRequest(@NotNull @Positive BigDecimal amount) {
}
