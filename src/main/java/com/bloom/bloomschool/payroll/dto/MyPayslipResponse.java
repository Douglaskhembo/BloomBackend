package com.bloom.bloomschool.payroll.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class MyPayslipResponse {
    private Long runId;
    private String monthLabel;
    private int year;
    private int monthIndex;
    private String runStatus;

    private double basicSalary;
    private double taxableAllowances;
    private double nonTaxableAllowances;
    private double grossSalary;
    private double nssf;
    private double nhif;
    private double housingLevy;
    private double paye;
    private double otherDeductions;
    private double totalDeductions;
    private double netSalary;

    private String paymentMethod;
    private String payoutDestination;
    private String lineStatus;
}
