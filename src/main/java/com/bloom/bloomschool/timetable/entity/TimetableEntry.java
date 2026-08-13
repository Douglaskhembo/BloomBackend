package com.bloom.bloomschool.timetable.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.subjects.entity.Subject;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.util.UUID;

/** One (gradeLevel, stream, dayOfWeek, period) -> (subject, teacher) assignment. Break periods
 *  never get an entry — the frontend never offers an assign affordance on a break row. */
@Entity
@Table(name = "bloom_sch_timetable_entries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"grade_level_id", "stream", "day_of_week", "period_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TimetableEntry extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "grade_level_id", nullable = false)
    private GradeLevel gradeLevel;

    /** "" for single-stream grades, same convention as ClassTeacherAssignment.stream. */
    @Column(nullable = false)
    private String stream;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "period_id", nullable = false)
    private TimetablePeriod period;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Staff teacher;
}
