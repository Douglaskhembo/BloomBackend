package com.bloom.bloomschool.staff.dto;

import com.bloom.bloomschool.gradeLevel.dto.GradeSummaryDto;
import com.bloom.bloomschool.staff.enums.EmploymentType;
import com.bloom.bloomschool.staff.enums.StaffType;
import com.bloom.bloomschool.student.dto.SubjectSummaryDto;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StaffResponseDto {
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    private StaffRoleResponseDto role;
    private EmploymentType employmentType;
    private Boolean isActive;
    private List<SubjectSummaryDto> subjects;
    private List<GradeSummaryDto> grades;
    private LocalDate dateOfBirth;
    private String idNumber;
    private String address;
    private String practiceNumber;
    private StaffType staffType;
    private String qualification;
    private String experience;
    private LocalDate joinedDate;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private List<String> documents = new ArrayList<>();
}
