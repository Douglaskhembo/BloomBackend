package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "payroll_settings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollSettings extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    private double personalRelief;      // monthly KES e.g. 2400
    private double insuranceRelief;     // annual max KES e.g. 5000
    private int payDay;                 // day of month e.g. 28; 0 = last day
    private String paymentMethod;       // bank_transfer | mpesa | cheque
    private String currency;            // KES
}
