package com.bloom.bloomschool.student.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SubjectSummaryDto {
    private UUID uuid;
    private String name;
    private String subjectCode;
    private Boolean isActive; }

