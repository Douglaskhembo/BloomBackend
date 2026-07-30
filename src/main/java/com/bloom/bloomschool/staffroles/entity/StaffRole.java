package com.bloom.bloomschool.staffroles.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bloom_sch_staff_roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffRole extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
    @Builder.Default
    private boolean active = true;
}
