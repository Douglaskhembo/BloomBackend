package com.bloom.bloomschool.staff.dto;

import com.bloom.bloomschool.staff.enums.EmploymentType;
import com.bloom.bloomschool.staff.enums.StaffType;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StaffRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    private UUID role;
    private EmploymentType employmentType;
    private Boolean isActive;
    private List<UUID> subjectUuids;
    private List<UUID> gradeUuids;
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
