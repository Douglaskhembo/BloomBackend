package com.bloom.bloomschool.attendance.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A physical fingerprint/face/card reader installed at a school location. Devices
 * authenticate their scan pushes to {@code POST /attendance/device-capture} with a
 * per-device API key (stored hashed, shown once at registration) rather than a JWT.
 */
@Entity
@Table(name = "bloom_sch_biometric_devices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BiometricDevice extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    /** Short human-assigned code, e.g. "MAIN-GATE-01" — this is what devices identify themselves as. */
    @Column(nullable = false, unique = true)
    private String deviceCode;

    @Column(nullable = false)
    private String name;

    /** Where it's physically installed, e.g. "Main Gate", "Staff Room", "Block B Entrance". */
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;

    /** BCrypt hash of the device's API key — the raw key is only ever shown once, at creation. */
    @Column(nullable = false)
    private String apiKeyHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DeviceStatus status = DeviceStatus.ACTIVE;

    private LocalDateTime lastSeenAt;

    public enum DeviceType { FINGERPRINT, FACE, CARD, OTHER }
    public enum DeviceStatus { ACTIVE, INACTIVE }
}
