package com.firstclub.membership.entity;

import com.firstclub.membership.exception.BusinessRuleException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

/**
 * A user's membership: which plan they pay for, which tier they currently hold, and its lifecycle.
 * Lifecycle transitions are enforced here so business rules cannot be bypassed by callers.
 *
 * <p>{@code @Version} enables optimistic locking: concurrent upgrade/downgrade/cancel/expire
 * operations on the same subscription are detected instead of silently overwriting each other.
 */
@Entity
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tier_id", nullable = false)
    private Tier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Version
    private long version;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    private Subscription(User user, Plan plan, Tier tier, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.plan = plan;
        this.tier = tier;
        this.status = SubscriptionStatus.ACTIVE;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Starts a new active subscription, computing expiry from the plan's billing cycle. */
    public static Subscription start(User user, Plan plan, Tier tier, LocalDate today) {
        return new Subscription(user, plan, tier, today, plan.expiryFrom(today));
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    /** Moves the membership to a different tier. Only an active subscription can change tier. */
    public void changeTier(Tier newTier) {
        requireActive("change tier");
        this.tier = newTier;
    }

    public void cancel() {
        requireActive("cancel");
        this.status = SubscriptionStatus.CANCELLED;
    }

    /** Marks an active subscription whose end date has passed as expired. */
    public void expire() {
        requireActive("expire");
        this.status = SubscriptionStatus.EXPIRED;
    }

    public boolean hasEnded(LocalDate today) {
        return today.isAfter(endDate);
    }

    private void requireActive(String action) {
        if (!isActive()) {
            throw new BusinessRuleException(
                    "Cannot %s a subscription that is %s".formatted(action, status));
        }
    }
}