package com.bloom.bloomschool.payroll.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.payroll.dto.*;
import com.bloom.bloomschool.payroll.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    // ── PAYE Bands ────────────────────────────────────────────────────────────

    @GetMapping("/paye-bands")
    public ResponseEntity<ApiResponse<?>> getPayeBands() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllPayeBands()));
    }

    @PostMapping("/paye-bands")
    public ResponseEntity<ApiResponse<?>> createPayeBand(@Valid @RequestBody PayeBandRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("PAYE band created", payrollService.createPayeBand(req)));
    }

    @PutMapping("/paye-bands/{id}")
    public ResponseEntity<ApiResponse<?>> updatePayeBand(@PathVariable Long id, @Valid @RequestBody PayeBandRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("PAYE band updated", payrollService.updatePayeBand(id, req)));
    }

    @DeleteMapping("/paye-bands/{id}")
    public ResponseEntity<ApiResponse<?>> deletePayeBand(@PathVariable Long id) {
        payrollService.deletePayeBand(id);
        return ResponseEntity.ok(ApiResponse.ok("PAYE band deleted"));
    }

    // ── NHIF Tiers ────────────────────────────────────────────────────────────

    @GetMapping("/nhif-tiers")
    public ResponseEntity<ApiResponse<?>> getNhifTiers() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllNhifTiers()));
    }

    @PostMapping("/nhif-tiers")
    public ResponseEntity<ApiResponse<?>> createNhifTier(@Valid @RequestBody NhifTierRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("NHIF tier created", payrollService.createNhifTier(req)));
    }

    @PutMapping("/nhif-tiers/{id}")
    public ResponseEntity<ApiResponse<?>> updateNhifTier(@PathVariable Long id, @Valid @RequestBody NhifTierRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("NHIF tier updated", payrollService.updateNhifTier(id, req)));
    }

    @DeleteMapping("/nhif-tiers/{id}")
    public ResponseEntity<ApiResponse<?>> deleteNhifTier(@PathVariable Long id) {
        payrollService.deleteNhifTier(id);
        return ResponseEntity.ok(ApiResponse.ok("NHIF tier deleted"));
    }

    // ── Allowance Types ───────────────────────────────────────────────────────

    @GetMapping("/allowance-types")
    public ResponseEntity<ApiResponse<?>> getAllowanceTypes() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllAllowanceTypes()));
    }

    @PostMapping("/allowance-types")
    public ResponseEntity<ApiResponse<?>> createAllowanceType(@Valid @RequestBody AllowanceTypeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Allowance type created", payrollService.createAllowanceType(req)));
    }

    @PutMapping("/allowance-types/{id}")
    public ResponseEntity<ApiResponse<?>> updateAllowanceType(@PathVariable Long id, @Valid @RequestBody AllowanceTypeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Allowance type updated", payrollService.updateAllowanceType(id, req)));
    }

    @PatchMapping("/allowance-types/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggleAllowanceType(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Toggled", payrollService.toggleAllowanceType(id)));
    }

    @DeleteMapping("/allowance-types/{id}")
    public ResponseEntity<ApiResponse<?>> deleteAllowanceType(@PathVariable Long id) {
        payrollService.deleteAllowanceType(id);
        return ResponseEntity.ok(ApiResponse.ok("Allowance type deleted"));
    }

    // ── Other Deductions ──────────────────────────────────────────────────────

    @GetMapping("/other-deductions")
    public ResponseEntity<ApiResponse<?>> getOtherDeductions() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllOtherDeductions()));
    }

    @PostMapping("/other-deductions")
    public ResponseEntity<ApiResponse<?>> createOtherDeduction(@Valid @RequestBody OtherDeductionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Deduction created", payrollService.createOtherDeduction(req)));
    }

    @PutMapping("/other-deductions/{id}")
    public ResponseEntity<ApiResponse<?>> updateOtherDeduction(@PathVariable Long id, @Valid @RequestBody OtherDeductionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Deduction updated", payrollService.updateOtherDeduction(id, req)));
    }

    @PatchMapping("/other-deductions/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggleOtherDeduction(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Toggled", payrollService.toggleOtherDeduction(id)));
    }

    @DeleteMapping("/other-deductions/{id}")
    public ResponseEntity<ApiResponse<?>> deleteOtherDeduction(@PathVariable Long id) {
        payrollService.deleteOtherDeduction(id);
        return ResponseEntity.ok(ApiResponse.ok("Deduction deleted"));
    }

    // ── Statutory Deductions ──────────────────────────────────────────────────

    @GetMapping("/statutory-deductions")
    public ResponseEntity<ApiResponse<?>> getStatutoryDeductions() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllStatutoryDeductions()));
    }

    @PostMapping("/statutory-deductions")
    public ResponseEntity<ApiResponse<?>> createStatutoryDeduction(@Valid @RequestBody StatutoryDeductionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Statutory deduction created", payrollService.createStatutoryDeduction(req)));
    }

    @PutMapping("/statutory-deductions/{id}")
    public ResponseEntity<ApiResponse<?>> updateStatutoryDeduction(@PathVariable Long id, @Valid @RequestBody StatutoryDeductionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Statutory deduction updated", payrollService.updateStatutoryDeduction(id, req)));
    }

    @PatchMapping("/statutory-deductions/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggleStatutoryDeduction(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Toggled", payrollService.toggleStatutoryDeduction(id)));
    }

    @DeleteMapping("/statutory-deductions/{id}")
    public ResponseEntity<ApiResponse<?>> deleteStatutoryDeduction(@PathVariable Long id) {
        payrollService.deleteStatutoryDeduction(id);
        return ResponseEntity.ok(ApiResponse.ok("Statutory deduction deleted"));
    }

    // ── Payroll Settings ──────────────────────────────────────────────────────

    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<?>> getSettings() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getSettings()));
    }

    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<?>> saveSettings(@Valid @RequestBody PayrollSettingsRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Settings saved", payrollService.saveSettings(req)));
    }

    // ── Staff Salaries ────────────────────────────────────────────────────────

    @GetMapping("/staff-salaries")
    public ResponseEntity<ApiResponse<?>> getAllSalaries() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllStaffSalaries()));
    }

    @GetMapping("/staff-salaries/{staffId}")
    public ResponseEntity<ApiResponse<?>> getSalary(@PathVariable String staffId) {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getStaffSalary(staffId)));
    }

    @PostMapping("/staff-salaries")
    public ResponseEntity<ApiResponse<?>> saveSalary(@Valid @RequestBody StaffSalaryRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Salary saved", payrollService.saveStaffSalary(req)));
    }

    @DeleteMapping("/staff-salaries/{staffId}")
    public ResponseEntity<ApiResponse<?>> deleteSalary(@PathVariable String staffId) {
        payrollService.deleteStaffSalary(staffId);
        return ResponseEntity.ok(ApiResponse.ok("Salary record deleted"));
    }

    // ── Payroll Runs ──────────────────────────────────────────────────────────

    @GetMapping("/runs")
    public ResponseEntity<ApiResponse<?>> getRuns() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllRuns()));
    }

    @GetMapping("/runs/{id}")
    public ResponseEntity<ApiResponse<?>> getRun(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getRun(id)));
    }

    @PostMapping("/runs/process")
    public ResponseEntity<ApiResponse<?>> processPayroll(
            @RequestParam int year,
            @RequestParam int monthIndex,
            @RequestParam String monthLabel) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payroll processed", payrollService.processPayroll(year, monthIndex, monthLabel)));
    }
}
