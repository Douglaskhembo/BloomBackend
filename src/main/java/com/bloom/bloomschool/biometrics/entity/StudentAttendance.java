package com.bloom.bloomschool.biometrics.entity;

import com.bloom.bloomschool.biometrics.util.AttendanceStatus;
import com.bloom.bloomschool.biometrics.util.EventType;
import com.bloom.bloomschool.students.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_attendance",
        indexes = {
                @Index(name = "idx_student_att_student", columnList = "student_id"),
                @Index(name = "idx_student_att_date",    columnList = "attendance_date")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentAttendance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** The bio profile that triggered this event */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bio_data_id", nullable = false)
    private StudentBioData bioData;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    /** Device that captured the event */
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EventType eventType = EventType.ENTRY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    private String remarks;
}
