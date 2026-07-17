package com.bloom.bloomschool.biometrics.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AttendanceResponse {
    private UUID uuid;
    private String ownerUuid;       // staff or student UUID
    private String ownerName;
    private String ownerRef;        // staffId or admissionNumber
    private LocalDate attendanceDate;
    private LocalDateTime clockInOrEntry;
    private LocalDateTime clockOutOrExit;
    private String deviceId;
    private String eventType;
    private String status;
    private String remarks;
}
