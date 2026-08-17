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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Category category = Category.OTHER;

    private double value;
    private Double maxAmount; // null = no cap
    private Double minAmount;
    private Double thresholdAmount;

    private boolean employerContribution;
    private double employerValue;

    @Builder.Default
    private boolean active = true;

    public enum ValueType { PERCENTAGE, FIXED, TIERED }
    public enum Category { NSSF, HOUSING_LEVY, SHIF, OTHER }
}
