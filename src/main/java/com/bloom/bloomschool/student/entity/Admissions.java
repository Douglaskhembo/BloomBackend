package com.bloom.bloomschool.student.entity;

import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.student.enums.ApplicationStage;
import com.bloom.bloomschool.student.enums.StudentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

import static com.bloom.bloomschool.student.enums.ApplicationStage.APPLICATION_REVIEW;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "bloom_school_admissions")
public class Admissions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, unique = true)
    private UUID uuid;
    @Column(unique = true, updatable = false, nullable = false)
    private String applicationId;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String birthCertificateNumber;
    private String address;
    private String medicalNote;
    private String stream;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ApplicationStage applicationStage = APPLICATION_REVIEW;
    @Enumerated(EnumType.STRING)
    private StudentType studentType;
    private String parentName;
    private String parentRelationship;
    private String parentPhoneNumber;
    private String parentEmail;
    @ManyToOne
    @JoinColumn(name = "grade_level_id")
    private GradeEntity gradeEntity;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private Student student;

}
