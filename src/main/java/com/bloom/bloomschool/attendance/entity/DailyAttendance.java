package com.bloom.bloomschool.attendance.entity;

import jakarta.persistence.*;
import lombok.*;
import com.bloom.bloomschool.student.entity.Student;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bloom_school_daily_attendance")
public class DailyAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendance_id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid(){
        if(uuid == null ) {
            uuid = UUID.randomUUID();
        }
    }
    @Column(nullable = false)
    private LocalDateTime markedAt;

    @Column(nullable = false)
    private String markedBy;

    @Column(nullable = false)
    private LocalDateTime attendanceDate;

    @Column(nullable = false)
    private String stream;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private  AttendanceStatus status;

    @Column(nullable = false)
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
