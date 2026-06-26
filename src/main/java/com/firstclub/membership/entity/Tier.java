package com.firstclub.membership.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A loyalty tier with its unlocked benefits and qualification criteria. {@code level} defines
 * the ordering used for upgrades/downgrades (higher level = higher tier).
 */
@Entity
@Table(name = "tiers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TierName name;

    @Column(nullable = false)
    private int level;

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Benefit> benefits = new ArrayList<>();

    @OneToOne(mappedBy = "tier", cascade = CascadeType.ALL, orphanRemoval = true)
    private TierCriteria criteria;

    public boolean isHigherThan(Tier other) {
        return this.level > other.level;
    }
}