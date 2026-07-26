package com.bloom.bloomschool.school.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class GradeLevelRequest {
    @NotBlank private String name;
    @NotNull private Integer displayOrder;
    private int streams = 1;
    /** Required (exactly `streams` entries) when streams > 1; ignored otherwise. */
    private List<String> streamNames;
    private Long id;
    private UUID uuid;
}
