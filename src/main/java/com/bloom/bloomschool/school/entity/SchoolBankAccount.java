package com.bloom.bloomschool.school.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.payroll.entity.Bank;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * One of the school's own bank accounts. At most one may be flagged
 * {@code useForPayroll} at a time — that's the debit account written to the payroll
 * bank-submission export.
 */
@Entity
@Table(name = "bloom_sch_bank_accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolBankAccount extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Column(nullable = false)
    private String accountNumber;

    private String accountName;
    private String branch;

    @Builder.Default
    private boolean useForPayroll = false;

    @Builder.Default
    private boolean active = true;
}
