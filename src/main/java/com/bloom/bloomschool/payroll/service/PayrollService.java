package com.bloom.bloomschool.payroll.service;

import com.bloom.bloomschool.payroll.dto.*;
import com.bloom.bloomschool.payroll.entity.*;
import com.bloom.bloomschool.payroll.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollService {

    private final PayeBandRepository payeBandRepo;
    private final NhifTierRepository nhifTierRepo;
    private final AllowanceTypeRepository allowanceTypeRepo;
    private final OtherDeductionRepository otherDeductionRepo;
    private final PayrollSettingsRepository settingsRepo;
    private final StaffSalaryRepository staffSalaryRepo;
    private final PayrollRunRepository payrollRunRepo;
    private final KenyaPayrollEngine engine;

    // ── PAYE Bands ────────────────────────────────────────────────────────────

    public List<PayeBand> getAllPayeBands() {
        return payeBandRepo.findAllByOrderByDisplayOrderAsc();
    }

    @Transactional
    public PayeBand createPayeBand(PayeBandRequest req) {
        return payeBandRepo.save(PayeBand.builder()
                .minAmount(req.getMinAmount())
                .maxAmount(req.getMaxAmount())
                .rate(req.getRate())
                .displayOrder(req.getDisplayOrder())
                .build());
    }

    @Transactional
    public PayeBand updatePayeBand(Long id, PayeBandRequest req) {
        PayeBand b = payeBandRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PAYE band not found"));
        b.setMinAmount(req.getMinAmount());
        b.setMaxAmount(req.getMaxAmount());
        b.setRate(req.getRate());
        b.setDisplayOrder(req.getDisplayOrder());
        return payeBandRepo.save(b);
    }

    @Transactional
    public void deletePayeBand(Long id) {
        payeBandRepo.deleteById(id);
    }

    // ── NHIF Tiers ────────────────────────────────────────────────────────────

    public List<NhifTier> getAllNhifTiers() {
        return nhifTierRepo.findAllByOrderByDisplayOrderAsc();
    }

    @Transactional
    public NhifTier createNhifTier(NhifTierRequest req) {
        return nhifTierRepo.save(NhifTier.builder()
                .minSalary(req.getMinSalary())
                .maxSalary(req.getMaxSalary())
                .amount(req.getAmount())
                .displayOrder(req.getDisplayOrder())
                .build());
    }

    @Transactional
    public NhifTier updateNhifTier(Long id, NhifTierRequest req) {
        NhifTier t = nhifTierRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NHIF tier not found"));
        t.setMinSalary(req.getMinSalary());
        t.setMaxSalary(req.getMaxSalary());
        t.setAmount(req.getAmount());
        t.setDisplayOrder(req.getDisplayOrder());
        return nhifTierRepo.save(t);
    }

    @Transactional
    public void deleteNhifTier(Long id) {
        nhifTierRepo.deleteById(id);
    }

    // ── Allowance Types ───────────────────────────────────────────────────────

    public List<AllowanceType> getAllAllowanceTypes() {
        return allowanceTypeRepo.findAll();
    }

    @Transactional
    public AllowanceType createAllowanceType(AllowanceTypeRequest req) {
        if (allowanceTypeRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Allowance type '" + req.getName() + "' already exists");
        return allowanceTypeRepo.save(AllowanceType.builder()
                .name(req.getName())
                .type(req.getType())
                .defaultValue(req.getDefaultValue())
                .taxable(req.isTaxable())
                .build());
    }

    @Transactional
    public AllowanceType updateAllowanceType(Long id, AllowanceTypeRequest req) {
        AllowanceType a = allowanceTypeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Allowance type not found"));
        a.setName(req.getName());
        a.setType(req.getType());
        a.setDefaultValue(req.getDefaultValue());
        a.setTaxable(req.isTaxable());
        return allowanceTypeRepo.save(a);
    }

    @Transactional
    public AllowanceType toggleAllowanceType(Long id) {
        AllowanceType a = allowanceTypeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Allowance type not found"));
        a.setActive(!a.isActive());
        return allowanceTypeRepo.save(a);
    }

    @Transactional
    public void deleteAllowanceType(Long id) {
        allowanceTypeRepo.deleteById(id);
    }

    // ── Other Deductions ──────────────────────────────────────────────────────

    public List<OtherDeduction> getAllOtherDeductions() {
        return otherDeductionRepo.findAll();
    }

    @Transactional
    public OtherDeduction createOtherDeduction(OtherDeductionRequest req) {
        if (otherDeductionRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Deduction '" + req.getName() + "' already exists");
        return otherDeductionRepo.save(OtherDeduction.builder()
                .name(req.getName())
                .type(req.getType())
                .defaultValue(req.getDefaultValue())
                .mandatory(req.isMandatory())
                .build());
    }

    @Transactional
    public OtherDeduction updateOtherDeduction(Long id, OtherDeductionRequest req) {
        OtherDeduction d = otherDeductionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deduction not found"));
        d.setName(req.getName());
        d.setType(req.getType());
        d.setDefaultValue(req.getDefaultValue());
        d.setMandatory(req.isMandatory());
        return otherDeductionRepo.save(d);
    }

    @Transactional
    public OtherDeduction toggleOtherDeduction(Long id) {
        OtherDeduction d = otherDeductionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deduction not found"));
        d.setActive(!d.isActive());
        return otherDeductionRepo.save(d);
    }

    @Transactional
    public void deleteOtherDeduction(Long id) {
        otherDeductionRepo.deleteById(id);
    }

    // ── Payroll Settings ──────────────────────────────────────────────────────

    public PayrollSettings getSettings() {
        return settingsRepo.findAll().stream().findFirst()
                .orElse(defaultSettings());
    }

    @Transactional
    public PayrollSettings saveSettings(PayrollSettingsRequest req) {
        PayrollSettings s = settingsRepo.findAll().stream().findFirst()
                .orElse(new PayrollSettings());
        s.setPersonalRelief(req.getPersonalRelief());
        s.setInsuranceRelief(req.getInsuranceRelief());
        s.setPayDay(req.getPayDay());
        s.setPaymentMethod(req.getPaymentMethod());
        s.setCurrency(req.getCurrency() != null ? req.getCurrency() : "KES");
        return settingsRepo.save(s);
    }

    // ── Staff Salaries ────────────────────────────────────────────────────────

    public List<StaffSalary> getAllStaffSalaries() {
        return staffSalaryRepo.findAll();
    }

    public StaffSalary getStaffSalary(String staffId) {
        return staffSalaryRepo.findByStaffId(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Salary not configured for staff: " + staffId));
    }

    @Transactional
    public StaffSalary saveStaffSalary(StaffSalaryRequest req) {
        StaffSalary s = staffSalaryRepo.findByStaffId(req.getStaffId())
                .orElse(StaffSalary.builder().staffId(req.getStaffId()).build());
        s.setBasicSalary(req.getBasicSalary());
        if (req.getAllowances() != null) s.setAllowances(req.getAllowances());
        if (req.getDeductions() != null) s.setDeductions(req.getDeductions());
        return staffSalaryRepo.save(s);
    }

    @Transactional
    public void deleteStaffSalary(String staffId) {
        staffSalaryRepo.findByStaffId(staffId).ifPresent(staffSalaryRepo::delete);
    }

    // ── Payroll Runs ──────────────────────────────────────────────────────────

    public List<PayrollRun> getAllRuns() {
        return payrollRunRepo.findAllByOrderByProcessedAtDesc();
    }

    public PayrollRun getRun(Long id) {
        return payrollRunRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payroll run not found"));
    }

    /**
     * Process payroll for all configured staff for the given month/year.
     * Idempotent — re-running the same month replaces the previous run.
     */
    @Transactional
    public PayrollRun processPayroll(int year, int monthIndex, String monthLabel) {
        List<PayeBand> bands = payeBandRepo.findAllByOrderByDisplayOrderAsc();
        List<NhifTier> tiers = nhifTierRepo.findAllByOrderByDisplayOrderAsc();
        List<AllowanceType> allowanceTypes = allowanceTypeRepo.findAll();
        List<StaffSalary> salaries = staffSalaryRepo.findAll();

        PayrollRun run = PayrollRun.builder()
                .monthLabel(monthLabel)
                .year(year)
                .monthIndex(monthIndex)
                .processedAt(LocalDateTime.now())
                .lines(new ArrayList<>())
                .build();

        for (StaffSalary sal : salaries) {
            if (sal.getBasicSalary() <= 0) continue;

            double taxable = 0, nonTaxable = 0, otherDed = 0;

            for (Map.Entry<Long, Double> entry : sal.getAllowances().entrySet()) {
                AllowanceType at = allowanceTypes.stream()
                        .filter(a -> a.getId().equals(entry.getKey()) && a.isActive())
                        .findFirst().orElse(null);
                if (at == null) continue;
                if (at.isTaxable()) taxable += entry.getValue();
                else nonTaxable += entry.getValue();
            }

            for (double amount : sal.getDeductions().values()) {
                otherDed += amount;
            }

            PayrollLineResult result = engine.calculate(
                    sal.getStaffId(), sal.getStaffId(),
                    sal.getBasicSalary(), taxable, nonTaxable, otherDed,
                    bands, tiers);

            PayrollLine line = PayrollLine.builder()
                    .payrollRun(run)
                    .staffId(result.getStaffId())
                    .staffName(result.getStaffName())
                    .basicSalary(result.getBasic())
                    .taxableAllowances(result.getTaxableAllowances())
                    .nonTaxableAllowances(result.getNonTaxableAllowances())
                    .grossSalary(result.getGross())
                    .nssf(result.getNssf())
                    .nhif(result.getNhif())
                    .housingLevy(result.getHousingLevy())
                    .paye(result.getPaye())
                    .otherDeductions(result.getOtherDeductions())
                    .totalDeductions(result.getTotalDeductions())
                    .netSalary(result.getNet())
                    .status(PayrollLine.Status.PAID)
                    .build();

            run.getLines().add(line);
        }

        return payrollRunRepo.save(run);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PayrollSettings defaultSettings() {
        return PayrollSettings.builder()
                .personalRelief(2400)
                .insuranceRelief(5000)
                .payDay(28)
                .paymentMethod("bank_transfer")
                .currency("KES")
                .build();
    }
}
