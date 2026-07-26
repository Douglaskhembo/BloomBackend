package com.bloom.bloomschool.payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/** Full audit trail of every action taken against a PayrollRun's approval workflow. */
@Entity
@Table(name = "bloom_sch_payroll_run_approvals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollRunApproval {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_run_id", nullable = false)
    @JsonIgnore
    private PayrollRun payrollRun;

    private Integer stepOrder;
    private String stepLabel;

    @Column(nullable = false)
    private String actorUuid;

    @Column(nullable = false)
    private String actorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @Column(length = 2000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime actedAt;

    public enum Action { SUBMITTED, APPROVED, REJECTED, SENT_TO_BANK }
}
