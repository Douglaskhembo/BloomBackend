package com.bloom.bloomschool.users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bloom_school_user_permissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPermissionId;

    @Column(unique = true, nullable = false,updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid(){if(uuid == null)uuid = UUID.randomUUID();}

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Permissions permission;

    @Column(nullable = false)
    private String overrideType;
}

