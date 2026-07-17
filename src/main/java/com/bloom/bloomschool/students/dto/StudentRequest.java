package com.bloom.bloomschool.students.dto;

import com.bloom.bloomschool.students.entity.Student;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private Long id;
    private UUID uuid;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String medicalNotes;
    @NotBlank private String grade;
    private String stream;
    private String parentName;
    private String parentPhone;
    private String parentEmail;
    private Student.Status status;
}
