package com.bloom.bloomschool.biometrics.entity;

import com.bloom.bloomschool.biometrics.util.EnrollmentStatus;
import com.bloom.bloomschool.biometrics.util.FingerName;
import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.staff.entity.Staff;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "staff_bio_data")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffBioData extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false, unique = true)
    private Staff staff;

    /**
     * Left-hand fingerprint — opaque template ref/hash from the device, never raw biometric data.
     * leftFingerName = which finger was used e.g. THUMB, INDEX, MIDDLE, RING, LITTLE
     */
    @Column(nullable = false)
    private String leftFingerprintTemplateRef;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FingerName leftFingerName;

    /**
     * Right-hand fingerprint — same convention as left.
     */
    @Column(nullable = false)
    private String rightFingerprintTemplateRef;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FingerName rightFingerName;

    private String faceTemplateRef;

    /** ID of the device that enrolled this person */
    private String enrolledDeviceId;

    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
}
