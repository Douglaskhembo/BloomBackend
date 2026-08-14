package com.bloom.bloomschool.assessments.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.students.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/** A single student's score on one {@link Assessment}. Null score = not yet graded. */
@Entity
@Table(name = "bloom_sch_assessment_marks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"assessment_id", "student_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentMark extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private Double score;
}
