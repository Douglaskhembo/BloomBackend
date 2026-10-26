package com.bloom.bloomschool.feestructure.entity;

import com.bloom.bloomschool.feestructure.enumerations.FeeApplicability;
import com.bloom.bloomschool.feestructure.enumerations.FeeCategory;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "bloom_school_fee_Item")
public class FeeItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    private String term;

    private Double amount;

    private Double termOneAmount;

    private Double termTwoAmount;

    private Double termThreeAmount;

    @ManyToMany
    @JoinTable(
            name = "bloom_school_Grade_feeItem",
            joinColumns = @JoinColumn(name = "feeItem_id"),
            inverseJoinColumns = @JoinColumn(name = "grade_level_id"))
    private Set<GradeEntity> gradeLevel = new HashSet<>();

    private Boolean mandatory;

    private boolean active = true;

    @Enumerated(EnumType.STRING)
    private FeeCategory feeCategory;

    @Enumerated(EnumType.STRING)
    private FeeApplicability feeApplicability;


}
