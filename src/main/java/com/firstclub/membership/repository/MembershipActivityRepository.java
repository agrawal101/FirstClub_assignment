package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipActivityRepository extends JpaRepository<MembershipActivity, Long> {

    Optional<MembershipActivity> findByUserId(Long userId);

    /** All activity rows whose monthly window opened before the given date — used by the reset scheduler. */
    List<MembershipActivity> findByWindowStartBefore(java.time.LocalDate date);
}
