package com.bloom.bloomschool.assessment.entity;

import com.bloom.bloomschool.assessment.AssessmentType;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.staff.entity.StaffEntity;
import com.bloom.bloomschool.subject.entity.SubjectEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bloom_school_assessment")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String term;

    private int maxScore = 100;
    @Column(unique = true)
    private int year;

    private String stream;

    @Enumerated(EnumType.STRING)
    private AssessmentType assessmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", unique = true)
    private GradeEntity grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", unique = true)
    private SubjectEntity subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private StaffEntity staff;

}
