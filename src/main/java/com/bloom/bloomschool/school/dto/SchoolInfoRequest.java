package com.bloom.bloomschool.school.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class SchoolInfoRequest {
    @NotBlank private String name;
    private String registrationNumber;
    @Email private String email;
    private Long id;
    private UUID uuid;
    private String phone;
    private String county;
    private String subCounty;
    private String postalAddress;
    private String physicalAddress;
    private String website;
}

