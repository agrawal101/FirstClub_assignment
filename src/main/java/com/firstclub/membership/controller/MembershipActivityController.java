package com.firstclub.membership.controller;

import com.firstclub.membership.dto.ApiResponse;
import com.firstclub.membership.dto.OrderRecordedResponse;
import com.firstclub.membership.dto.RecordOrderRequest;
import com.firstclub.membership.service.MembershipActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/orders")
@RequiredArgsConstructor
public class MembershipActivityController {

    private final MembershipActivityService membershipActivityService;

    @PostMapping
    public ApiResponse<OrderRecordedResponse> recordOrder(@PathVariable Long userId,
                                                          @Valid @RequestBody RecordOrderRequest request) {
        return ApiResponse.ok(membershipActivityService.recordOrder(userId, request.amount()));
    }
}
