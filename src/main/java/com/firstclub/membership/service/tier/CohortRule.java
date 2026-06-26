package com.firstclub.membership.service.tier;

import com.firstclub.membership.entity.MembershipActivity;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import org.springframework.stereotype.Component;

/**
 * Qualifies when the tier requires no specific cohort, or the user belongs to the required one.
 */
@Component
public class CohortRule implements TierRule {

    @Override
    public boolean isSatisfied(TierCriteria criteria, MembershipActivity activity, User user) {
        return criteria.getRequiredCohort() == null
                || criteria.getRequiredCohort() == user.getCohort();
    }
}
