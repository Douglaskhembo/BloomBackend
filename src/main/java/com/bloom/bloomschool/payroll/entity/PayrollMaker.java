package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * A user authorized to generate/submit payroll runs (the "maker" side of the maker-checker workflow).
 * Multiple makers are allowed (e.g. Finance Officer + a backup) — set up under Payroll Setup, same as
 * approval steps. Being added here is what grants the ability (auto-grants PAYROLL_RUN), rather than
 * requiring the permission to be granted elsewhere first.
 */
@Entity
@Table(name = "bloom_sch_payroll_makers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollMaker extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
