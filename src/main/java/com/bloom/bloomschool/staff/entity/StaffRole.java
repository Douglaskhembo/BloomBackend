package com.bloom.bloomschool.staff.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "bloom_school_staff_role"
)
public class StaffRole {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column( updatable = false, unique = true, nullable = false)
    private UUID uuid;

    private String name;

    private String description;

    private boolean status;
}
