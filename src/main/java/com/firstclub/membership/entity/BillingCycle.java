package com.firstclub.membership.entity;

/**
 * Billing cadence a user chooses when subscribing. The number of months is the single
 * source of truth used to compute a subscription's expiry date.
 */
public enum BillingCycle {

    MONTHLY(1),
    QUARTERLY(3),
    YEARLY(12);

    private final int months;

    BillingCycle(int months) {
        this.months = months;
    }

    public int months() {
        return months;
    }
}