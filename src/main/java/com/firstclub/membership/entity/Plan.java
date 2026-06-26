package com.firstclub.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A purchasable membership plan: a billing cycle with its price. Tiers are a separate axis
 * (loyalty level) and do not affect the plan price.
 */
@Entity
@Table(name = "plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private BillingCycle billingCycle;

    @Column(nullable = false)
    private BigDecimal price;

    public Plan(BillingCycle billingCycle, BigDecimal price) {
        this.billingCycle = billingCycle;
        this.price = price;
    }

    /** Expiry date for a subscription on this plan that starts on {@code start}. */
    public LocalDate expiryFrom(LocalDate start) {
        return start.plusMonths(billingCycle.months());
    }
}