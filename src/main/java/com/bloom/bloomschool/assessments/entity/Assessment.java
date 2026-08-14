package com.bloom.bloomschool.assessments.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.subjects.entity.Subject;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/** One gradebook column: a named CAT/Exam for a (subject, gradeLevel, stream, term, year),
 *  owned by the teacher who created it. Marks live in {@link AssessmentMark}. */
@Entity
@Table(name = "bloom_sch_assessments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"subject_id", "grade_level_id", "stream", "term", "year", "name"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Assessment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private String term;

    @Column(nullable = false)
    private int year;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "grade_level_id", nullable = false)
    private GradeLevel gradeLevel;

    /** "" for single-stream grades, same convention as TimetableEntry.stream. */
    @Column(nullable = false)
    private String stream;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Staff teacher;

    @Builder.Default
    @Column(nullable = false)
    private double maxScore = 100;

    public enum Type { CAT, EXAM }
}
