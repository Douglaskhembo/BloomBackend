package com.bloom.bloomschool.payroll.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PayrollLineResult {
    private Long id;
    private UUID uuid;
    private String staffId;
    private String staffName;
    private double basic;
    private double taxableAllowances;
    private double nonTaxableAllowances;
    private double gross;
    private double nssf;
    private double nhif;
    private double housingLevy;
    private double paye;
    private double otherDeductions;
    private double totalDeductions;
    private double net;
}
