package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * A settlement rail used in the payroll bank-submission export's "Payment Type" column
 * (e.g. PESALINK, RTGS, EFT, WITHIN BANK for bank transfers; MPESA, AIRTEL MONEY for
 * mobile wallets). {@code code} is always stored upper-cased regardless of input case.
 */
@Entity
@Table(name = "bloom_sch_payment_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentType extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false, unique = true)
    private String code;

    @Builder.Default
    private boolean active = true;

    public enum Category { BANK, MOBILE_WALLET }
}
