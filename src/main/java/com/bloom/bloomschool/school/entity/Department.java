package com.bloom.bloomschool.school.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "bloom_school_department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long department_id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) uuid = UUID.randomUUID();
    }

    @Column(nullable = false)
    private String departmentName;

    private String description;

    @Column(unique = true, nullable = false)
    private String departmentCode;

    @ManyToMany(mappedBy = "departments")
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();


}