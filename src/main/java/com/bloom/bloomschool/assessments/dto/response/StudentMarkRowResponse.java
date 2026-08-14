package com.bloom.bloomschool.assessments.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentMarkRowResponse {
    private UUID studentUuid;
    private String admissionNumber;
    private String studentName;
    private Double score;
}
