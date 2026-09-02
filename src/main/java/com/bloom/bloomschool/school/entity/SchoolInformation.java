package com.bloom.bloomschool.school.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "school_information")
public class SchoolInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long information_id;

    @Column(unique = true, nullable = false,updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) uuid = UUID.randomUUID();
    }

    @Column(unique = true, nullable = false)
    private String schoolName;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Column(unique = true, nullable = false)
    private String schoolCode;

    @Column(nullable = false)
    private String schoolEmail;

    @Column(nullable = false)
    private String schoolPhone;

    @Column(nullable = false)
    private String schoolAddress;

    @Column(nullable = false)
    private int establishedYear;

    @Column(nullable = true)
    private String website;

    @Column(nullable = false)
    private String logoUrl;

    private boolean hasBranch;

    private boolean hasDepartment;

}
