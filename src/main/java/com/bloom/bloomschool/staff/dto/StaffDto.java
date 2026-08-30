package com.bloom.bloomschool.staff.dto;

import com.bloom.bloomschool.staff.EmploymentType;
import com.bloom.bloomschool.subject.dto.SubjectDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String role;
    private EmploymentType employmentType;
    private Boolean isActive;
    private List<UUID> subjectUuids;
    private List<SubjectDto> subjects;
}
