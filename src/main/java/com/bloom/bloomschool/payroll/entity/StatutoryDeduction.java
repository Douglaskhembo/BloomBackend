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
     * Which statutory calculation this row feeds. NSSF and HOUSING_LEVY rows are summed
     * (percentage of gross, or fixed, each capped at maxAmount if set) to compute net pay.
     * OTHER rows are informational only, same as TIERED rows (NHIF has its own tier table).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Category category = Category.OTHER;

    private double value;
    private Double maxAmount; // null = no cap

    private boolean employerContribution;
    private double employerValue;

    @Builder.Default
    private boolean active = true;

    public enum ValueType { PERCENTAGE, FIXED, TIERED }
    public enum Category { NSSF, HOUSING_LEVY, OTHER }
}
