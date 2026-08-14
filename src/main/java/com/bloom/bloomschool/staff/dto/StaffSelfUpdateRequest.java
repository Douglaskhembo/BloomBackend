package com.bloom.bloomschool.staff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * What a staff member may edit about their own record via self-service — deliberately a
 * narrow subset of {@link StaffRequest}. HR-controlled fields (staffType, staffRole, status,
 * employmentType, subjects, grade, joined date, etc.) are excluded on purpose so a teacher
 * can't grant themselves a different role or employment status through this endpoint.
 */
@Data
public class StaffSelfUpdateRequest {
    @NotBlank private String phone;
    @NotBlank @Email private String email;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
}
