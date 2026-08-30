package com.bloom.bloomschool.subject.entity;

import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bloom_school_subject")

public class SubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column( updatable = false, unique = true, nullable = false)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String subjectCode;

    @Column(nullable = false)
    private Boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "bloom_subject_grade_levels",
            joinColumns = @JoinColumn(name = "subject_uuid"),
            inverseJoinColumns = @JoinColumn(name = "grade_uuid")
    )
    private List<GradeEntity> grades = new ArrayList<>();

}