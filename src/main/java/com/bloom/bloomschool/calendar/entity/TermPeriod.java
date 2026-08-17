package com.bloom.bloomschool.calendar.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_term_periods", uniqueConstraints = @UniqueConstraint(columnNames = {"academic_year", "term"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TermPeriod extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    /** "Term 1" | "Term 2" | "Term 3" — matches the term labels used by the fee structure workflow. */
    @Column(nullable = false)
    private String term;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
}
