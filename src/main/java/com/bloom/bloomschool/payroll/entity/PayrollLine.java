package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bloom_sch_payroll_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollLine extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_run_id", nullable = false)
    private PayrollRun payrollRun;

    @Column(nullable = false)
    private String staffId;

    private String staffName;

    private double basicSalary;
    private double taxableAllowances;
    private double nonTaxableAllowances;
    private double grossSalary;
    private double nssf;
    private double nhif;
    private double housingLevy;
    private double paye;
    private double otherDeductions;
    private double totalDeductions;
    private double netSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PAID;

    public enum Status { PAID, PENDING }
}
