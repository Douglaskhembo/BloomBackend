package com.bloom.bloomschool.bills.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.suppliers.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_bills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bill extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    /** Optional link to a registered supplier — a bill can also be raised against a free-text supplier name. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    /** Snapshot of the supplier's name at bill-creation time (or a manually typed name if no supplier link). */
    @Column(nullable = false)
    private String supplierName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDateTime paidDate;

    /** Ground truth is just UNPAID/PAID — "overdue" is derived at read time from dueDate, never stored. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.UNPAID;

    public enum Status { UNPAID, PAID }
}
