package com.bloom.bloomschool.student.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentSummaryDto {
    private UUID uuid;
    private String admissionNumber;
    private String fullName;
}
