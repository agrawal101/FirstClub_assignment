package com.firstclub.membership.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Consistent envelope for every API response so clients can rely on one shape.
 * On success {@code error} is null; on failure {@code data} is null.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean success, T data, ApiError error) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> failure(ApiError error) {
        return new ApiResponse<>(false, null, error);
    }
}