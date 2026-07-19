package com.bloom.bloomschool.fees.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeeStructureLineRequest {
    @NotNull private Long itemId;
    private boolean enabled;
    private double amount;
}
