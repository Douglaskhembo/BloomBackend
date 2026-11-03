package com.bloom.bloomschool.student.dto;

import com.bloom.bloomschool.gradeLevel.dto.GradeSummaryDto;
import com.bloom.bloomschool.student.enums.ApplicationStage;
import com.bloom.bloomschool.student.enums.StudentType;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AdmissionResponseDto {
    private UUID uuid;
    private String applicationId;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String birthCertificateNumber;
    private String address;
    private String medicalNote;
    private String stream;
    private ApplicationStage applicationStage;
    private StudentType studentType;
    private String parentName;
    private String parentRelationship;
    private String parentPhoneNumber;
    private String parentEmail;
    private GradeSummaryDto grade;
    private UUID studentUuid;
}