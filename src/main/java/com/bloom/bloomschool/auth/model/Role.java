package com.bloom.bloomschool.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Entity @Table(name = "bloom_sys_role") @Builder
public class Role {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "bloom_sys_role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @Builder.Default @ToString.Exclude @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties("module")
    private Set<Permission> permissions = new HashSet<>();
}
