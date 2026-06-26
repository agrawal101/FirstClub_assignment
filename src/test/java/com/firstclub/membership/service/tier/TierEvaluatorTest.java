package com.firstclub.membership.service.tier;

import com.firstclub.membership.entity.Cohort;
import com.firstclub.membership.entity.MembershipActivity;
import com.firstclub.membership.entity.Tier;
import com.firstclub.membership.entity.TierName;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.repository.TierRepository;
import com.firstclub.membership.support.TestEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TierEvaluatorTest {

    private final TierRepository tierRepository = mock(TierRepository.class);

    private final Tier silver = TestEntities.tier(1L, TierName.SILVER, 1, TestEntities.criteria(0, "0", null));
    private final Tier gold = TestEntities.tier(2L, TierName.GOLD, 2, TestEntities.criteria(5, "5000", null));
    private final Tier platinum =
            TestEntities.tier(3L, TierName.PLATINUM, 3, TestEntities.criteria(15, "20000", Cohort.PREMIUM));

    private TierEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new TierEvaluator(
                List.of(new OrderCountRule(), new MonthlySpendRule(), new CohortRule()), tierRepository);
        when(tierRepository.findAllByOrderByLevelDesc()).thenReturn(List.of(platinum, gold, silver));
    }

    @Test
    void newMemberFallsBackToEntryTier() {
        User user = new User("Ann", "ann@example.com", Cohort.REGULAR);

        Tier result = evaluator.evaluate(user, activity(user, 0, "0"));

        assertThat(result.getName()).isEqualTo(TierName.SILVER);
    }

    @Test
    void meetingOrderAndSpendThresholdsReachesGold() {
        User user = new User("Bob", "bob@example.com", Cohort.REGULAR);

        Tier result = evaluator.evaluate(user, activity(user, 6, "6000"));

        assertThat(result.getName()).isEqualTo(TierName.GOLD);
    }

    @Test
    void premiumMemberMeetingAllCriteriaReachesPlatinum() {
        User user = new User("Cara", "cara@example.com", Cohort.PREMIUM);

        Tier result = evaluator.evaluate(user, activity(user, 20, "25000"));

        assertThat(result.getName()).isEqualTo(TierName.PLATINUM);
    }

    @Test
    void cohortMismatchBlocksPlatinumAndFallsBackToGold() {
        User regular = new User("Dan", "dan@example.com", Cohort.REGULAR);

        Tier result = evaluator.evaluate(regular, activity(regular, 20, "25000"));

        assertThat(result.getName()).isEqualTo(TierName.GOLD);
    }

    private MembershipActivity activity(User user, int orderCount, String monthlySpend) {
        MembershipActivity activity = new MembershipActivity(user, LocalDate.of(2026, 1, 1));
        ReflectionTestUtils.setField(activity, "orderCount", orderCount);
        ReflectionTestUtils.setField(activity, "monthlySpend", new BigDecimal(monthlySpend));
        return activity;
    }
}
