package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_payroll_runs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollRun extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private String monthLabel;   // e.g. "April 2025"

    private int year;
    private int monthIndex;      // 0-based

    private LocalDateTime processedAt;

    @OneToMany(mappedBy = "payrollRun", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PayrollLine> lines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.DRAFT;

    /** Whoever generated this run — cannot also approve/reject it (self-approval block). */
    private String makerUuid;
    private String makerName;

    private LocalDateTime submittedAt;

    /** sequenceOrder of the PayrollWorkflowStep currently awaiting action; null once resolved. */
    private Integer currentStepOrder;

    @Column(length = 2000)
    private String rejectionReason;

    private LocalDateTime updatedAt;

    private String sentToBankByUuid;
    private String sentToBankByName;
    private LocalDateTime sentToBankAt;

    public enum Status { DRAFT, PENDING_APPROVAL, REJECTED, APPROVED, SENT_TO_BANK }
}
