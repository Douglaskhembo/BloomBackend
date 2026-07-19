package com.bloom.bloomschool.students.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.students.util.Stage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_admissions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Admission extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(unique = true, nullable = false)
    private String applicationId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String medicalNotes;
    private String grade;
    private String stream;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grade_level_id")
    private GradeLevel gradeLevel;

    @OneToOne(mappedBy = "admission", fetch = FetchType.LAZY)
    @JsonIgnore
    private Student student;

    private String parentName;
    private String parentRelationship;
    private String parentPhone;
    private String parentEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Stage stage = Stage.APPLICATION_REVIEW;

    /**
     * Set only in-memory (never persisted) right after enrollment, so the stage-update
     * response can surface a freshly created parent account's one-time temp password
     * without changing the response DTO shape for any other caller of this entity.
     */
    @Transient
    private Boolean parentAccountCreated;
    @Transient
    private String parentAccountUserName;
    @Transient
    private String parentTemporaryPassword;
}
