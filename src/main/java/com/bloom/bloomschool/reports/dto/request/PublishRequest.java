package com.bloom.bloomschool.reports.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PublishRequest {
    @NotNull
    private UUID gradeLevelUuid;

    private String stream = "";

    @NotBlank
    private String term;

    @NotNull
    private Integer year;
}
