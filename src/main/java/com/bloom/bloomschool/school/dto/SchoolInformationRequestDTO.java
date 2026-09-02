package com.bloom.bloomschool.school.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SchoolInformationRequestDTO {

    private String schoolName;

    private String registrationNumber;

    private String schoolCode;

    private String schoolEmail;

    private String schoolPhone;

    private String schoolAddress;

    private int establishedYear;

    private String website;

    private String logoUrl;

    private boolean hasBranch;

    private boolean hasDepartment;
}
