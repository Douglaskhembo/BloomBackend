package com.bloom.bloomschool.students.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "admissions")
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

    private String parentName;
    private String parentRelationship;
    private String parentPhone;
    private String parentEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Stage stage = Stage.APPLICATION_REVIEW;

    public enum Stage {
        APPLICATION_REVIEW,
        INTERVIEW_SCHEDULED,
        OFFER_SENT,
        FEE_PAYMENT,
        ENROLLED,
        REJECTED
    }
}
