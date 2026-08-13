package com.bloom.bloomschool.fees.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.school.entity.GradeLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_fee_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private String name;

    private String description;
    private double amount;

    // Empty set = applies to all grades
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "bloom_sch_fee_item_grade_levels",
            joinColumns = @JoinColumn(name = "fee_item_id"),
            inverseJoinColumns = @JoinColumn(name = "grade_level_id"))
    @Builder.Default
    private Set<GradeLevel> gradeLevels = new HashSet<>();

    @Builder.Default
    private String term = "Per Term";   // "Per Term" | "Per Year" | "One-time"

    @Enumerated(EnumType.STRING)
    private FeeCategory category;   // nullable — legacy items stay uncategorized until edited

    /** Wrapper Boolean, not primitive — a primitive would force ddl-auto=update to add this
     *  column NOT NULL with no default, which fails against the already-populated table. */
    private Boolean mandatory;

    @Builder.Default
    private boolean active = true;
}
