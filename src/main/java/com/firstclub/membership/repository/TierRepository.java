package com.firstclub.membership.repository;

import com.firstclub.membership.entity.Tier;
import com.firstclub.membership.entity.TierName;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TierRepository extends JpaRepository<Tier, Long> {

    /** Tiers ordered best-first, with benefits and criteria eagerly loaded for evaluation/display. */
    @EntityGraph(attributePaths = {"benefits", "criteria"})
    List<Tier> findAllByOrderByLevelDesc();

    @EntityGraph(attributePaths = {"benefits", "criteria"})
    Optional<Tier> findByName(TierName name);
}
