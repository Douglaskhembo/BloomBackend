package com.bloom.bloomschool.student.dto;

import lombok.*;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.*;
import com.bloom.bloomschool.attendance.dto.DailyAttendanceResponseDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {
    private UUID uuid;
    private String admissionNumber;
    private String fullName;
    private String studentEmail;
    private String entryNumber;
    private LocalDate dateOfBirth;
    private String status;
    private List<DailyAttendanceResponseDTO> attendances;
}