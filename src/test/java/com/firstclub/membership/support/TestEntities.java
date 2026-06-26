package com.firstclub.membership.support;

import com.firstclub.membership.entity.Cohort;
import com.firstclub.membership.entity.Tier;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.TierName;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Builds reference entities ({@link Tier}, {@link TierCriteria}) for tests. These are seeded by
 * Flyway in production and never constructed in application code, so they have no public
 * constructors — tests hydrate them via reflection rather than polluting the entities.
 */
public final class TestEntities {

    private TestEntities() {
    }

    public static TierCriteria criteria(int minOrderCount, String minMonthlySpend, Cohort requiredCohort) {
        TierCriteria criteria = instantiate(TierCriteria.class);
        ReflectionTestUtils.setField(criteria, "minOrderCount", minOrderCount);
        ReflectionTestUtils.setField(criteria, "minMonthlySpend", new BigDecimal(minMonthlySpend));
        ReflectionTestUtils.setField(criteria, "requiredCohort", requiredCohort);
        return criteria;
    }

    public static Tier tier(Long id, TierName name, int level, TierCriteria criteria) {
        Tier tier = instantiate(Tier.class);
        ReflectionTestUtils.setField(tier, "id", id);
        ReflectionTestUtils.setField(tier, "name", name);
        ReflectionTestUtils.setField(tier, "level", level);
        ReflectionTestUtils.setField(tier, "benefits", new ArrayList<>());
        ReflectionTestUtils.setField(tier, "criteria", criteria);
        return tier;
    }

    private static <T> T instantiate(Class<T> type) {
        try {
            var constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not instantiate " + type, e);
        }
    }
}
