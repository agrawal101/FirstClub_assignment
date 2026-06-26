package com.firstclub.membership.entity;

/**
 * Loyalty tiers a member progresses through. Ordering (which tier is "higher") is stored
 * as a configurable {@code level} on the {@link Tier} entity rather than relying on enum order.
 */
public enum TierName {
    SILVER,
    GOLD,
    PLATINUM
}