package com.bloom.bloomschool.attendance.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.staff.entity.Staff;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bloom_sch_class_teachers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"grade_level_id", "stream"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClassTeacherAssignment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false, unique = true)
    private Staff teacher;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "grade_level_id", nullable = false)
    private GradeLevel gradeLevel;

    @Column(nullable = false)
    private String stream;
}
