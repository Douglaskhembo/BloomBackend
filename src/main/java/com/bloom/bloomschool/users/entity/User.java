package com.bloom.bloomschool.users.entity;

import com.bloom.bloomschool.staff.entity.StaffEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "bloom_school_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Column(unique = true, nullable = false)
    private String username;

    private String otherNames;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    private String sessionToken;

    private String profileRef;

    @OneToOne
    @JoinColumn(name = "staff_id")
    private StaffEntity staff;

    @Builder.Default
    private boolean active = false;

    @Builder.Default
    private boolean firstLogin = true;

    @Builder.Default
    private boolean accountLocked = false;

    @Builder.Default
    private Integer failedLoginAttempts = 0;

    private boolean enable2FA;

    @Column(nullable = false)
    private LocalDateTime passwordExpiry;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "bloom_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Roles> roles = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        if (this.passwordExpiry == null) {
            this.passwordExpiry = LocalDateTime.now().plusDays(90);
        }
    }

}