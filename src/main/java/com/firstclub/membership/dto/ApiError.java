package com.firstclub.membership.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Error payload. {@code fieldErrors} is populated only for validation failures.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(String message, List<FieldError> fieldErrors) {

    public ApiError(String message) {
        this(message, null);
    }

    public record FieldError(String field, String message) {
    }
}