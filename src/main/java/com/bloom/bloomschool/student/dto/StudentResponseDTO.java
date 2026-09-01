package com.bloom.bloomschool.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

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
}