package com.bloom.bloomschool.student.dto;

import com.bloom.bloomschool.student.enums.StudentType;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AdmissionRequestDto {
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String birthCertificateNumber;
    private String address;
    private String medicalNote;
    private String stream;
    private StudentType studentType;
    private String parentName;
    private String parentRelationship;
    private String parentPhoneNumber;
    private String parentEmail;
    private UUID gradeUuid;
}