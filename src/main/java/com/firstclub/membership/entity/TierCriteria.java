package com.firstclub.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Configurable thresholds a user must meet to qualify for a tier. Each field maps to one
 * {@code TierRule} strategy; a null {@code requiredCohort} means the cohort rule is not enforced.
 */
@Entity
@Table(name = "tier_criteria")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TierCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "tier_id", nullable = false, unique = true)
    private Tier tier;

    @Column(nullable = false)
    private int minOrderCount;

    @Column(nullable = false)
    private BigDecimal minMonthlySpend;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_cohort")
    private Cohort requiredCohort;
}