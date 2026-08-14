package com.bloom.bloomschool.assessments.dto.request;

import com.bloom.bloomschool.assessments.entity.Assessment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class AssessmentRequest {
    @NotBlank
    private String name;

    @NotNull
    private Assessment.Type type;

    @NotBlank
    private String term;

    @NotNull
    private Integer year;

    @NotNull
    private UUID subjectUuid;

    @NotNull
    private UUID gradeLevelUuid;

    /** "" for single-stream grades. */
    private String stream = "";

    @Positive
    private Double maxScore = 100.0;
}
