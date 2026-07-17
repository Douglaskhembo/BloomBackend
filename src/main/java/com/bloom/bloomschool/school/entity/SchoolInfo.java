package com.bloom.bloomschool.school.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bloom_sch_school_info")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolInfo extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private String name;

    private String registrationNumber;
    private String email;
    private String phone;
    private String county;
    private String subCounty;
    private String postalAddress;
    private String physicalAddress;
    private String website;
    private String logoUrl;
    private boolean hasBranch;
    private boolean hasDepartment;
}
