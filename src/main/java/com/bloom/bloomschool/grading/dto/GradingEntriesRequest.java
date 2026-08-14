package com.bloom.bloomschool.grading.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GradingEntriesRequest {
    @NotNull @Valid private List<GradingEntryRequest> entries;
}
