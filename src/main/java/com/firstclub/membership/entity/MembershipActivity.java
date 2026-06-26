package com.firstclub.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Rolling record of a user's purchasing activity. Drives automatic tier evaluation.
 * {@code monthlySpend} accumulates within the current window; {@code totalSpend} is lifetime.
 */
@Entity
@Table(name = "membership_activity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MembershipActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private int orderCount;

    @Column(nullable = false)
    private BigDecimal monthlySpend;

    @Column(nullable = false)
    private BigDecimal totalSpend;

    @Column(nullable = false)
    private LocalDate windowStart;

    public MembershipActivity(User user, LocalDate windowStart) {
        this.user = user;
        this.windowStart = windowStart;
        this.orderCount = 0;
        this.monthlySpend = BigDecimal.ZERO;
        this.totalSpend = BigDecimal.ZERO;
    }

    /** Records a completed order against both the monthly window and the lifetime total. */
    public void recordOrder(BigDecimal amount) {
        this.orderCount++;
        this.monthlySpend = this.monthlySpend.add(amount);
        this.totalSpend = this.totalSpend.add(amount);
    }

    /** Starts a fresh monthly spend window; lifetime totals are untouched. */
    public void resetMonthlyWindow(LocalDate today) {
        this.monthlySpend = BigDecimal.ZERO;
        this.windowStart = today;
    }
}