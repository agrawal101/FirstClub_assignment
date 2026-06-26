package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipActivityRepository extends JpaRepository<MembershipActivity, Long> {

    Optional<MembershipActivity> findByUserId(Long userId);
}
