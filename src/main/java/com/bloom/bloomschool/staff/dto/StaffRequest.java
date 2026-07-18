package com.bloom.bloomschool.staff.dto;

import com.bloom.bloomschool.staff.util.EmploymentType;
import com.bloom.bloomschool.staff.util.StaffType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class StaffRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private Long id;
    private UUID uuid;
    private String gender;
    private LocalDate dateOfBirth;
    private String idNumber;
    @NotBlank private String phone;
    @Email private String email;
    private String address;
    private String practiceNumber;
    @NotNull private StaffType staffType;
    @NotNull private EmploymentType employmentType;
    private Integer contractPeriodMonths;
    private String subject;
    private String grade;
    private String qualification;
    private String experience;
    private LocalDate joined;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
}
