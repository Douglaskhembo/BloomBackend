package com.bloom.bloomschool.subject.dto;

import java.util.List;
import java.util.UUID;
import lombok.*;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SubjectRequestDto {
    private String name;
    private String subjectCode;
    private Boolean isActive;
    private List<UUID> gradeUuids;
}

