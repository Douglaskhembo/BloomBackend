package com.bloom.bloomschool.feestructure.entity;

import com.bloom.bloomschool.feestructure.enumerations.FeeStructureStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bloom_school_fee_Structure")
public class FeeStructureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    private int academicYear;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String term;

    @Enumerated(EnumType.STRING)
    private FeeStructureStatus status;

    private int version;

    private String comment;

    private String rejectReason;

    @ElementCollection
    @CollectionTable(
            name = "bloom_school_feestructure_lines",
            joinColumns = @JoinColumn(name = "structure_id")
    )
    private List<FeeStructureLine> feeStructureLines = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "bloom_school_feestructure_baseline",
            joinColumns = @JoinColumn(name = "structure_id")
    )
    private List<FeeStructureLine> baseline = new ArrayList<>();

    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime updatedAt;
    private LocalDate dueDate;
}
