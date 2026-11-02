package com.bloom.bloomschool.users.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.UUID;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "bloom_school_roles")

public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, updatable = false)
    private Long roleId;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid(){if(uuid == null)uuid = UUID.randomUUID();}

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "bloom_roles_module",
            joinColumns = @JoinColumn(name = "roles_id"),
            inverseJoinColumns = @JoinColumn(name = "modules_id")
    )
    @Builder.Default
    private Set<Modules> modules = new HashSet<>();
}
