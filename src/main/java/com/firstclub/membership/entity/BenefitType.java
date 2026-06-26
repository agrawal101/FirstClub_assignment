package com.firstclub.membership.entity;

/**
 * Kinds of perk a tier can unlock. The concrete value (e.g. discount percentage) is stored
 * per tier on the {@link Benefit} entity so perks stay configurable without code changes.
 */
public enum BenefitType {
    FREE_DELIVERY,
    DISCOUNT_PERCENT,
    EXCLUSIVE_DEALS,
    PRIORITY_SUPPORT
}