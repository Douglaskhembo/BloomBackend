package com.bloom.bloomschool.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bloom_sch_module")
@Builder
public class SysModule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    private String moduleName;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<Permission> permissions = new HashSet<>();

    public void addPermission(Permission p) { p.setModule(this); permissions.add(p); }
    public void removePermission(Permission p) { p.setModule(null); permissions.remove(p); }
}
