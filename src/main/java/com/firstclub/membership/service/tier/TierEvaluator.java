package com.firstclub.membership.service.tier;

import com.firstclub.membership.entity.MembershipActivity;
import com.firstclub.membership.entity.Tier;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.repository.TierRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Decides which tier a user currently qualifies for by applying every {@link TierRule} against
 * the tier's criteria, from the highest tier down. The first tier whose rules all pass wins.
 *
 * <p>Spring injects all rule beans, so a new criterion is added by creating a new {@link TierRule}
 * — this class never changes.
 */
@Component
public class TierEvaluator {

    private final List<TierRule> rules;
    private final TierRepository tierRepository;

    public TierEvaluator(List<TierRule> rules, TierRepository tierRepository) {
        this.rules = rules;
        this.tierRepository = tierRepository;
    }

    public Tier evaluate(User user, MembershipActivity activity) {
        return tierRepository.findAllByOrderByLevelDesc().stream()
                .filter(tier -> qualifies(tier, user, activity))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No tier qualifies for user " + user.getId() + "; an entry tier should always match"));
    }

    private boolean qualifies(Tier tier, User user, MembershipActivity activity) {
        return rules.stream().allMatch(rule -> rule.isSatisfied(tier.getCriteria(), activity, user));
    }
}
