package com.bloom.bloomschool.fees.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_fee_structures")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeStructure extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private int academicYear;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String term;

    @Column(nullable = false)
    private int version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(nullable = false)
    private String maker;

    private String approver;

    @Column(length = 2000)
    private String note;

    @Column(length = 2000)
    private String rejectionReason;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime reviewedAt;

    @ElementCollection
    @CollectionTable(name = "bloom_sch_fee_structure_lines", joinColumns = @JoinColumn(name = "structure_id"))
    @Builder.Default
    private List<FeeStructureLine> lines = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "bloom_sch_fee_structure_baseline", joinColumns = @JoinColumn(name = "structure_id"))
    @Builder.Default
    private List<FeeStructureLine> baseline = new ArrayList<>();

    public enum Status { DRAFT, PENDING_APPROVAL, REJECTED, APPROVED }
}
