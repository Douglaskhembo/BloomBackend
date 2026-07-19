package com.bloom.bloomschool.payroll.service;

import com.bloom.bloomschool.payroll.dto.PayrollLineResult;
import com.bloom.bloomschool.payroll.entity.NhifTier;
import com.bloom.bloomschool.payroll.entity.PayeBand;
import com.bloom.bloomschool.payroll.entity.StatutoryDeduction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Kenya statutory payroll engine. PAYE bands, NHIF tiers, NSSF/Housing Levy rates and
 * personal relief are all configured via the Payroll Setup screens (PayeBand, NhifTier,
 * StatutoryDeduction, PayrollSettings) rather than hardcoded — mirrors the frontend
 * lib/payroll/kenya.ts so both sides stay in sync.
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
            List<NhifTier> nhifTiers,
            List<StatutoryDeduction> statutoryDeductions,
            double personalRelief) {

        double gross = basic + taxableAllowances + nonTaxableAllowances;
        double nssf = sumStatutory(gross, statutoryDeductions, StatutoryDeduction.Category.NSSF);
        double nhif = computeNhif(gross, nhifTiers);
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
     * Sums active, non-tiered deductions in the given category (percentage of gross, or
     * fixed, each capped at maxAmount if set). NSSF's two-tier structure is modeled as two
     * rows (e.g. "NSSF Tier I"/"Tier II", each 6% capped at their KES limit) rather than a
     * single band-based formula, matching what the Statutory Deductions setup screen offers.
     */
    private double sumStatutory(double gross, List<StatutoryDeduction> deductions, StatutoryDeduction.Category category) {
        double total = 0;
        for (StatutoryDeduction d : deductions) {
            if (!d.isActive() || d.getCategory() != category || d.getType() == StatutoryDeduction.ValueType.TIERED) continue;
            double amount = d.getType() == StatutoryDeduction.ValueType.PERCENTAGE ? gross * (d.getValue() / 100.0) : d.getValue();
            if (d.getMaxAmount() != null) amount = Math.min(amount, d.getMaxAmount());
            total += amount;
        }
        return Math.round(total);
    }

    private double computeNhif(double gross, List<NhifTier> tiers) {
        return tiers.stream()
                .filter(t -> gross >= t.getMinSalary() && (t.getMaxSalary() == null || gross <= t.getMaxSalary()))
                .mapToDouble(NhifTier::getAmount)
                .findFirst()
                .orElse(0);
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
