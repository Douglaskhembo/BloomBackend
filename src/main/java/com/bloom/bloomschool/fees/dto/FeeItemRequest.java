package com.bloom.bloomschool.fees.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class FeeItemRequest {
    @NotBlank private String name;
    private String description;
    @NotNull private Double amount;
    private String grade;
    private String term = "Per Term";
    private boolean active = true;
    private Long id;
    private UUID uuid;
}
