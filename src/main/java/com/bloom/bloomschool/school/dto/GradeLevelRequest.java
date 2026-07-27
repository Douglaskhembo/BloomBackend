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
    /** Max students for the single stream; used only when streams == 1. Null/0 = unlimited. */
    private Integer capacity;
    /** Required (exactly `streams` entries, same order as streamNames) when streams > 1; ignored otherwise.
     *  A null/0 entry means unlimited for that stream. */
    private List<Integer> streamCapacities;
    private Long id;
    private UUID uuid;
}
