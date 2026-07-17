package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bloom_sch_paye_bands")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayeBand extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    private double minAmount;
    private Double maxAmount; // null = no upper limit
    private double rate;      // percentage e.g. 10.0 = 10%
    private int displayOrder;
}
