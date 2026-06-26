package com.firstclub.membership.controller;

import com.firstclub.membership.dto.ApiResponse;
import com.firstclub.membership.dto.PlanResponse;
import com.firstclub.membership.dto.TierResponse;
import com.firstclub.membership.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/plans")
    public ApiResponse<List<PlanResponse>> getPlans() {
        return ApiResponse.ok(catalogService.listPlans());
    }

    @GetMapping("/tiers")
    public ApiResponse<List<TierResponse>> getTiers() {
        return ApiResponse.ok(catalogService.listTiers());
    }
}
