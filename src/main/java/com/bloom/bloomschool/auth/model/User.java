package com.bloom.bloomschool.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bloom_sch_users")
@Builder
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(unique = true, nullable = false)
    private String userName;

    private String firstName;
    private String otherNames;
    private String password;
    private String email;
    private String phoneNumber;

    @Builder.Default
    private boolean active = false;
    @Builder.Default
    private boolean firstLogin = true;

    @Column(columnDefinition = "boolean default false")
    private boolean enable2FA;

    @Column(name = "session_token")
    private String sessionToken;

    @Column(name = "password_expiry")
    private LocalDateTime passwordExpiry;

    /**
     * profileRef — UUID of the linked domain entity:
     *   ADMIN   → SystemUser.uuid
     *   TEACHER → Staff.uuid
     *   PARENT  → Student parent record uuid
     */
    @Column(name = "profile_ref")
    private String profileRef;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "bloom_sch_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}
