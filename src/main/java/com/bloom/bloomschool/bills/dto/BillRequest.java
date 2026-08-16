package com.bloom.bloomschool.bills.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BillRequest {
    private Long supplierId;
    private String supplierName;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    private LocalDate dueDate;
}
