package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "staff_salaries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffSalary extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(unique = true, nullable = false)
    private String staffId;

    private double basicSalary;

    // allowanceTypeId -> amount
    @ElementCollection
    @CollectionTable(name = "staff_salary_allowances", joinColumns = @JoinColumn(name = "staff_salary_id"))
    @MapKeyColumn(name = "allowance_type_id")
    @Column(name = "amount")
    @Builder.Default
    private Map<Long, Double> allowances = new HashMap<>();

    // otherDeductionId -> amount
    @ElementCollection
    @CollectionTable(name = "staff_salary_deductions", joinColumns = @JoinColumn(name = "staff_salary_id"))
    @MapKeyColumn(name = "deduction_type_id")
    @Column(name = "amount")
    @Builder.Default
    private Map<Long, Double> deductions = new HashMap<>();
}
