package com.bloom.bloomschool.student.dto;

import com.bloom.bloomschool.gradeLevel.dto.GradeDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.student.entity.Student;
import com.bloom.bloomschool.student.enums.ApplicationStage;
import com.bloom.bloomschool.student.enums.StudentType;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AdmissionsDto {
    private UUID uuid;
    private String applicationId;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String birthCertificateNumber;
    private String address;
    private String medicalNote;
    private String grade;
    private String stream;
    private ApplicationStage applicationStage;
    private StudentType studentType;
    private String parentName;
    private String parentRelationship;
    private String parentPhoneNumber;
    private String parentEmail;
    private UUID gradeUuid;
    private GradeDto gradeDto;
}
