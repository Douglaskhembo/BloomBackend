package com.bloom.bloomschool.reports.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.school.entity.GradeLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

/** Publish-state for one (gradeLevel, stream, term, year)'s term reports — report scores
 *  themselves are always computed on the fly from Assessment/AssessmentMark; this only gates
 *  whether parents may see them yet. No row = DRAFT. */
@Entity
@Table(name = "bloom_sch_report_publications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"grade_level_id", "stream", "term", "year"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportPublication extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "grade_level_id", nullable = false)
    private GradeLevel gradeLevel;

    @Column(nullable = false)
    private String stream;

    @Column(nullable = false)
    private String term;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publishedDate;

    public enum Status { DRAFT, PUBLISHED }
}
