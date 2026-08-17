package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


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
