package com.bloom.bloomschool.holidays.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_public_holidays")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Holiday extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    /** Informational only — admins re-enter a dated instance each year, no date-expansion logic. */
    @Builder.Default
    private boolean recurringAnnually = false;

    @Builder.Default
    private boolean active = true;
}
