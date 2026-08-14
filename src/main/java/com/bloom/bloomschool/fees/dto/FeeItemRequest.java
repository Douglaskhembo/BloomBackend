package com.bloom.bloomschool.fees.dto;

import com.bloom.bloomschool.fees.entity.FeeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class FeeItemRequest {
    @NotBlank private String name;
    private String description;
    @NotNull private Double amount;
    private Set<UUID> gradeLevelUuids;
    private String term = "Per Term";
    private Double term1Amount;
    private Double term2Amount;
    private Double term3Amount;
    private FeeCategory category;
    private Boolean mandatory = true;
    private boolean active = true;
    private Long id;
    private UUID uuid;
}
