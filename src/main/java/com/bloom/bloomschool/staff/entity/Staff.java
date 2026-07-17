package com.bloom.bloomschool.staff.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.staff.util.StaffType;
import com.bloom.bloomschool.staff.util.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_staff")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Staff extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(unique = true, nullable = false)
    private String staffId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String gender;
    private LocalDate dateOfBirth;

    @Column(unique = true)
    private String idNumber;

    @Column(nullable = false)
    private String phone;

    @Column(unique = true)
    private String email;

    private String address;
    private String practiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffType staffType;

    private String subject;
    private String grade;
    private String qualification;
    private String experience;
    private LocalDate joined;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
}
