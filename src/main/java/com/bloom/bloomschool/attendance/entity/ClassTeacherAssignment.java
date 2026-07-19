package com.bloom.bloomschool.attendance.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.staff.entity.Staff;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/** One class/homeroom teacher per grade+stream — scopes "my class" attendance views. */
@Entity
@Table(name = "bloom_sch_class_teachers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"grade", "stream"}))
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

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String stream;
}
