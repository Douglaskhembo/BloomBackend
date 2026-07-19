package com.bloom.bloomschool.fees.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FeeStructureSubmitRequest {
    @NotNull private Integer academicYear;
    @NotBlank private String grade;
    @NotBlank private String term;
    @NotEmpty @Valid private List<FeeStructureLineRequest> lines;
    private String note;
    @NotBlank private String maker;

    /** When set on /submit, reworks this rejected record in place instead of creating a new one. */
    private UUID reworkUuid;
}
