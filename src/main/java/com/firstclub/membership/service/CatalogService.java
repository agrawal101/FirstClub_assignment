package com.firstclub.membership.service;

import com.firstclub.membership.dto.PlanResponse;
import com.firstclub.membership.dto.TierResponse;
import com.firstclub.membership.mapper.PlanMapper;
import com.firstclub.membership.mapper.TierMapper;
import com.firstclub.membership.repository.PlanRepository;
import com.firstclub.membership.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Read-only catalog of what a user can subscribe to: the available plans and the tiers
 * (with their unlocked benefits) they can hold.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogService {

    private final PlanRepository planRepository;
    private final TierRepository tierRepository;

    public List<PlanResponse> listPlans() {
        return planRepository.findAll().stream()
                .map(PlanMapper::toResponse)
                .toList();
    }

    public List<TierResponse> listTiers() {
        return tierRepository.findAllByOrderByLevelDesc().stream()
                .map(TierMapper::toResponse)
                .toList();
    }
}
