package com.firstclub.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A single perk unlocked by a tier. {@code value} holds the perk's configurable detail,
 * e.g. "10" for DISCOUNT_PERCENT or "true" for FREE_DELIVERY.
 */
@Entity
@Table(name = "benefits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Benefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tier_id", nullable = false)
    private Tier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BenefitType type;

    @Column(name = "benefit_value", nullable = false)
    private String value;
}