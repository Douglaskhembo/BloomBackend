package com.bloom.bloomschool.grading.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GradingStructureRequest {
    @NotBlank private String grade;
    @NotNull @Valid private List<GradingEntryRequest> entries;
}
