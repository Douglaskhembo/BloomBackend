package com.bloom.bloomschool.payroll.dto;

import lombok.Builder;
import lombok.Data;

/**
 * A single staff member's own payslip line, scoped to their own data only — never the full
 * PayrollRun (which would leak every other staff member's pay). Only APPROVED/SENT_TO_BANK runs
 * are ever surfaced here; a run still being drafted/approved isn't final and shouldn't be visible
 * to the employee it concerns.
 */
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
