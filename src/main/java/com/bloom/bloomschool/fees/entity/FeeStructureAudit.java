package com.bloom.bloomschool.fees.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_fee_structure_audit")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeStructureAudit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private LocalDateTime at;

    @Column(nullable = false)
    private String actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String term;

    private Integer academicYear;

    @Column(length = 2000)
    private String comment;

    public enum Action { SAVED_DRAFT, SUBMITTED, APPROVED, REJECTED, REWORKED }
}
