package com.firstclub.membership.service.tier;

import com.firstclub.membership.entity.MembershipActivity;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;

/**
 * One qualification criterion for a tier (Strategy). A tier is awarded only when every rule
 * is satisfied. Adding a new way to qualify means adding a new implementation — no existing
 * code changes.
 */
public interface TierRule {

    boolean isSatisfied(TierCriteria criteria, MembershipActivity activity, User user);
}
