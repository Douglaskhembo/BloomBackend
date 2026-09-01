package com.bloom.bloomschool.student.dto;

import lombok.*;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {
    private String admissionNumber;
    private String firstName;
    private String lastName;
    private String studentEmail;
    private String entryNumber;
    private LocalDate dateOfBirth;
}