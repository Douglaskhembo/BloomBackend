package com.bloom.bloomschool.attendance.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
import com.bloom.bloomschool.attendance.entity.AttendanceStatus;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyAttendanceResponseDTO {
    private UUID uuid;
    private LocalDateTime markedAt;
    private String markedBy;
    private LocalDateTime attendanceDate;
    private  AttendanceStatus status;
    private String stream;
    private String grade;
    private UUID studentUuid;
    private String studentAdmissionNumber;

}