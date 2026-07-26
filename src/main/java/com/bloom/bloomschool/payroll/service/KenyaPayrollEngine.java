package com.bloom.bloomschool.payroll.service;

import com.bloom.bloomschool.payroll.dto.PayrollLineResult;
import com.bloom.bloomschool.payroll.entity.PayeBand;
import com.bloom.bloomschool.payroll.entity.StatutoryDeduction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Kenya statutory payroll engine. PAYE bands, NSSF/Housing Levy/SHIF rates and personal relief
 * are all configured via the Payroll Setup screens (PayeBand, StatutoryDeduction,
 * PayrollSettings) rather than hardcoded — mirrors the frontend lib/payroll/kenya.ts so both
 * sides stay in sync. SHIF (replacing NHIF since Oct 2024) is a flat percentage of gross like
 * NSSF/Housing Levy, not a tiered lookup, so it's a StatutoryDeduction row, not its own table.
 */
@Component
@RequiredArgsConstructor
public class KenyaPayrollEngine {

    public PayrollLineResult calculate(
            String staffId,
            String staffName,
            double basic,
            double taxableAllowances,
            double nonTaxableAllowances,
            double otherDeductions,
            List<PayeBand> payeBands,
            List<StatutoryDeduction> statutoryDeductions,
            double personalRelief) {

        double gross = basic + taxableAllowances + nonTaxableAllowances;
        double nssf = sumStatutory(gross, statutoryDeductions, StatutoryDeduction.Category.NSSF);
        double nhif = sumStatutory(gross, statutoryDeductions, StatutoryDeduction.Category.SHIF);
        double housingLevy = sumStatutory(gross, statutoryDeductions, StatutoryDeduction.Category.HOUSING_LEVY);

        // PAYE taxable = gross - non-taxable allowances - NSSF (deductible)
        double taxableIncome = Math.max(0, gross - nonTaxableAllowances - nssf);
        double paye = computePaye(taxableIncome, payeBands, personalRelief);

        double totalDeductions = nssf + nhif + housingLevy + paye + otherDeductions;
        double net = gross - totalDeductions;

        return PayrollLineResult.builder()
                .staffId(staffId)
                .staffName(staffName)
                .basic(basic)
                .taxableAllowances(taxableAllowances)
                .nonTaxableAllowances(nonTaxableAllowances)
                .gross(gross)
                .nssf(nssf)
                .nhif(nhif)
                .housingLevy(housingLevy)
                .paye(paye)
                .otherDeductions(otherDeductions)
                .totalDeductions(totalDeductions)
                .net(net)
                .build();
    }

    /**
     * Sums active, non-tiered deductions in the given category. Percentage rows apply to
     * (gross - thresholdAmount) rather than the full gross, so a tier can tax only the amount in
     * excess of a lower bound (e.g. NSSF Tier II only applies above the Tier I ceiling) — a null/0
     * threshold behaves exactly as before this concept existed. The result is then capped at
     * maxAmount and floored at minAmount if either is set (SHIF's KES 300 minimum, for example).
     */
    private double sumStatutory(double gross, List<StatutoryDeduction> deductions, StatutoryDeduction.Category category) {
        double total = 0;
        for (StatutoryDeduction d : deductions) {
            if (!d.isActive() || d.getCategory() != category || d.getType() == StatutoryDeduction.ValueType.TIERED) continue;
            double base = Math.max(0, gross - (d.getThresholdAmount() != null ? d.getThresholdAmount() : 0));
            double amount = d.getType() == StatutoryDeduction.ValueType.PERCENTAGE ? base * (d.getValue() / 100.0) : d.getValue();
            if (d.getMaxAmount() != null) amount = Math.min(amount, d.getMaxAmount());
            if (d.getMinAmount() != null) amount = Math.max(amount, d.getMinAmount());
            total += amount;
        }
        return Math.round(total);
    }

    private double computePaye(double taxableIncome, List<PayeBand> bands, double personalRelief) {
        double remaining = taxableIncome;
        double tax = 0;
        for (PayeBand band : bands) {
            if (remaining <= 0) break;
            double bandWidth = band.getMaxAmount() == null
                    ? remaining
                    : Math.min(remaining, band.getMaxAmount() - band.getMinAmount() + 1);
            tax += bandWidth * (band.getRate() / 100.0);
            remaining -= bandWidth;
        }
        return Math.max(0, Math.round(tax - personalRelief));
    }
}
