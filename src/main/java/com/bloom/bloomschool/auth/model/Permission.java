package com.bloom.bloomschool.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bloom_sch_permission")
@Builder
public class Permission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_module_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties("permissions")
    private SysModule module;

    private String permDesc;
    private String accessType;
}
