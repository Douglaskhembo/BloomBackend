package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "allowance_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AllowanceType extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ValueType type = ValueType.FIXED;

    private double defaultValue;
    private boolean taxable;
    @Builder.Default
    private boolean active = true;

    public enum ValueType { FIXED, PERCENTAGE }
}
