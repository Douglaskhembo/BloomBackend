package com.bloom.bloomschool.payroll.service;

import com.bloom.bloomschool.payroll.dto.PayrollLineResult;
import com.bloom.bloomschool.payroll.entity.NhifTier;
import com.bloom.bloomschool.payroll.entity.PayeBand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Kenya statutory payroll engine (2024/2025 rates).
 * Mirrors the frontend lib/payroll/kenya.ts so both sides stay in sync.
 */
@Component
@RequiredArgsConstructor
public class KenyaPayrollEngine {

    private static final double NSSF_TIER1_LIMIT = 7_000;
    private static final double NSSF_TIER2_LIMIT = 36_000;
    private static final double NSSF_RATE        = 0.06;
    private static final double HOUSING_LEVY_RATE = 0.015;
    private static final double PERSONAL_RELIEF   = 2_400;

    public PayrollLineResult calculate(
            String staffId,
            String staffName,
            double basic,
            double taxableAllowances,
            double nonTaxableAllowances,
            double otherDeductions,
            List<PayeBand> payeBands,
            List<NhifTier> nhifTiers) {

        double gross = basic + taxableAllowances + nonTaxableAllowances;
        double nssf  = computeNssf(gross);
        double nhif  = computeNhif(gross, nhifTiers);
        double housingLevy = Math.round(gross * HOUSING_LEVY_RATE);

        // PAYE taxable = gross - non-taxable allowances - NSSF (deductible)
        double taxableIncome = Math.max(0, gross - nonTaxableAllowances - nssf);
        double paye = computePaye(taxableIncome, payeBands);

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

    private double computeNssf(double gross) {
        double tier1 = Math.min(gross, NSSF_TIER1_LIMIT) * NSSF_RATE;
        double tier2Base = Math.max(0, Math.min(gross, NSSF_TIER2_LIMIT) - NSSF_TIER1_LIMIT);
        return Math.round(tier1 + tier2Base * NSSF_RATE);
    }

    private double computeNhif(double gross, List<NhifTier> tiers) {
        return tiers.stream()
                .filter(t -> gross >= t.getMinSalary() && (t.getMaxSalary() == null || gross <= t.getMaxSalary()))
                .mapToDouble(NhifTier::getAmount)
                .findFirst()
                .orElse(0);
    }

    private double computePaye(double taxableIncome, List<PayeBand> bands) {
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
        return Math.max(0, Math.round(tax - PERSONAL_RELIEF));
    }
}
