package com.firstclub.membership.controller;

import com.firstclub.membership.dto.ApiResponse;
import com.firstclub.membership.dto.ChangeTierRequest;
import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SubscriptionResponse> subscribe(@Valid @RequestBody SubscribeRequest request) {
        return ApiResponse.ok(subscriptionService.subscribe(request));
    }

    @GetMapping("/users/{userId}/subscription")
    public ApiResponse<SubscriptionResponse> getCurrent(@PathVariable Long userId) {
        return ApiResponse.ok(subscriptionService.getCurrentSubscription(userId));
    }

    @PatchMapping("/subscriptions/{id}/tier")
    public ApiResponse<SubscriptionResponse> changeTier(@PathVariable Long id,
                                                        @Valid @RequestBody ChangeTierRequest request) {
        return ApiResponse.ok(subscriptionService.changeTier(id, request.tier()));
    }

    @DeleteMapping("/subscriptions/{id}")
    public ApiResponse<SubscriptionResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok(subscriptionService.cancel(id));
    }
}
