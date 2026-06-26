package com.firstclub.membership.service.tier;

import com.firstclub.membership.entity.MembershipActivity;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import org.springframework.stereotype.Component;

/** Qualifies when the user has placed at least the tier's required number of orders. */
@Component
public class OrderCountRule implements TierRule {

    @Override
    public boolean isSatisfied(TierCriteria criteria, MembershipActivity activity, User user) {
        return activity.getOrderCount() >= criteria.getMinOrderCount();
    }
}
