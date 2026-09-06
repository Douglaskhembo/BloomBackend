package com.bloom.bloomschool.student.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {
    private UUID studentUuid;
    private String admissionNumber;
    private String firstName;
    private String lastName;
    private String studentEmail;
    private String entryNumber;
    private LocalDate dateOfBirth;
    private String studentName;
}