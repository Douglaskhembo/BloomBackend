package com.bloom.bloomschool.fees.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "fee_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private String name;

    private String description;
    private double amount;
    private String grade;   // null = applies to all grades
    @Builder.Default
    private boolean active = true;
}
