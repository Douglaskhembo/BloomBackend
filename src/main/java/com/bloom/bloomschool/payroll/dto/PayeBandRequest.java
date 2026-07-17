package com.bloom.bloomschool.payroll.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PayeBandRequest {
    @NotNull private Double minAmount;
    private Double maxAmount;
    @NotNull private Double rate;
    @NotNull private Integer displayOrder;
    private Long id;
    private UUID uuid;
}
