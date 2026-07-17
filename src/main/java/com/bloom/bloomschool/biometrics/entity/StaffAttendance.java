package com.bloom.bloomschool.biometrics.entity;

import com.bloom.bloomschool.biometrics.util.AttendanceStatus;
import com.bloom.bloomschool.biometrics.util.EventType;
import com.bloom.bloomschool.staff.entity.Staff;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_staff_attendance",
        indexes = {
                @Index(name = "idx_staff_att_staff", columnList = "staff_id"),
                @Index(name = "idx_staff_att_date",  columnList = "attendance_date")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffAttendance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    /** The bio profile that triggered this event — preserves audit trail even if bio data changes */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bio_data_id", nullable = false)
    private StaffBioData bioData;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false)
    private LocalDateTime clockIn;

    private LocalDateTime clockOut;

    /** Device that captured the event */
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EventType eventType = EventType.CLOCK_IN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    private String remarks;
}
