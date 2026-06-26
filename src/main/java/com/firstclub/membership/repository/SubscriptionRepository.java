package com.firstclub.membership.repository;

import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @EntityGraph(attributePaths = {"user", "plan", "tier"})
    Optional<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);

    /** Active subscriptions whose end date has already passed — used by the expiry scheduler. */
    @EntityGraph(attributePaths = {"user", "plan", "tier"})
    List<Subscription> findByStatusAndEndDateBefore(SubscriptionStatus status, LocalDate date);
}
