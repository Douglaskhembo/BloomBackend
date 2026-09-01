package com.bloom.bloomschool.school.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SchoolInformationResponseDTO {
    private UUID uuid;

    private String schoolName;

    private String schoolCode;

    private String registrationNumber;

    private String schoolEmail;

    private String schoolPhone;

    private String schoolAddress;

    private int establishedYear;

    private String website;

    private String logoUrl;

    private boolean hasBranch;

    private boolean hasDepartment;
}
