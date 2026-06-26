package com.firstclub.membership.service.tier;

import com.firstclub.membership.entity.MembershipActivity;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import org.springframework.stereotype.Component;

/** Qualifies when the user's spend in the current monthly window meets the tier's threshold. */
@Component
public class MonthlySpendRule implements TierRule {

    @Override
    public boolean isSatisfied(TierCriteria criteria, MembershipActivity activity, User user) {
        return activity.getMonthlySpend().compareTo(criteria.getMinMonthlySpend()) >= 0;
    }
}
