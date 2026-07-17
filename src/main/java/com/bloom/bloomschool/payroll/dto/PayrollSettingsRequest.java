package com.bloom.bloomschool.payroll.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PayrollSettingsRequest {
    @NotNull private Double personalRelief;
    @NotNull private Double insuranceRelief;
    @NotNull private Integer payDay;
    private String paymentMethod;
    private String currency;
    private Long id;
    private UUID uuid;
}
