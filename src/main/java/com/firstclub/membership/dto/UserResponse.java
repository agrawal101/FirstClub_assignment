package com.firstclub.membership.dto;

import com.firstclub.membership.entity.Cohort;

public record UserResponse(Long id, String name, String email, Cohort cohort) {
}
