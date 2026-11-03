package com.bloom.bloomschool.staff.dto;

import com.bloom.bloomschool.staff.EmploymentType;
import com.bloom.bloomschool.student.dto.SubjectSummaryDto;
import lombok.*;

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
    private String role;
    private EmploymentType employmentType;
    private Boolean isActive;
    private List<SubjectSummaryDto> subjects;
}
