package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bloom_sch_statutory_deductions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatutoryDeduction extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ValueType type = ValueType.PERCENTAGE;

    /**
     * Which statutory calculation this row feeds. NSSF, HOUSING_LEVY and SHIF rows are summed
     * (percentage of (gross - thresholdAmount), or fixed, each floored at minAmount and capped at
     * maxAmount if set) to compute net pay. OTHER/TIERED rows are informational only.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Category category = Category.OTHER;

    private double value;
    private Double maxAmount; // null = no cap

    /** Floor on the final computed deduction amount, e.g. SHIF's KES 300 minimum. Null = no floor. */
    private Double minAmount;

    /**
     * Lower bound subtracted from gross before applying the percentage — lets a tier apply only to
     * the *excess* above this amount (e.g. NSSF Tier II only taxes gross above the Tier I ceiling).
     * Null/0 = applies to the full gross, same as before this field existed.
     */
    private Double thresholdAmount;

    private boolean employerContribution;
    private double employerValue;

    @Builder.Default
    private boolean active = true;

    public enum ValueType { PERCENTAGE, FIXED, TIERED }
    public enum Category { NSSF, HOUSING_LEVY, SHIF, OTHER }
}
