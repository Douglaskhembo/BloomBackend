package com.bloom.bloomschool.payroll.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class NhifTierRequest {
    @NotNull private Double minSalary;
    private Double maxSalary;
    @NotNull private Double amount;
    @NotNull private Integer displayOrder;
    private Long id;
    private UUID uuid;
}
