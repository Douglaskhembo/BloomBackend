package com.bloom.bloomschool.payroll.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayrollDecisionRequest {
    @NotNull private Decision decision;
    private String comment;

    public enum Decision { APPROVE, REJECT }
}
