package com.bloom.bloomschool.payroll.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.payroll.dto.*;
import com.bloom.bloomschool.payroll.service.PayrollService;
import com.bloom.bloomschool.staff.dto.StaffPaymentDetailsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;
    private final PermissionResolver permissionResolver;

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

    // ── Banks ─────────────────────────────────────────────────────────────────

    @GetMapping("/banks")
    public ResponseEntity<ApiResponse<?>> getBanks() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllBanks()));
    }

    @PostMapping("/banks")
    public ResponseEntity<ApiResponse<?>> createBank(@Valid @RequestBody BankRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Bank added", payrollService.createBank(req)));
    }

    @PutMapping("/banks/{id}")
    public ResponseEntity<ApiResponse<?>> updateBank(@PathVariable Long id, @Valid @RequestBody BankRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Bank updated", payrollService.updateBank(id, req)));
    }

    @PatchMapping("/banks/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggleBank(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Toggled", payrollService.toggleBank(id)));
    }

    @DeleteMapping("/banks/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBank(@PathVariable Long id) {
        payrollService.deleteBank(id);
        return ResponseEntity.ok(ApiResponse.ok("Bank deleted"));
    }

    // ── Mobile Money Providers ────────────────────────────────────────────────

    @GetMapping("/mobile-money-providers")
    public ResponseEntity<ApiResponse<?>> getMobileMoneyProviders() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllMobileMoneyProviders()));
    }

    @PostMapping("/mobile-money-providers")
    public ResponseEntity<ApiResponse<?>> createMobileMoneyProvider(@Valid @RequestBody MobileMoneyProviderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Provider added", payrollService.createMobileMoneyProvider(req)));
    }

    @PutMapping("/mobile-money-providers/{id}")
    public ResponseEntity<ApiResponse<?>> updateMobileMoneyProvider(@PathVariable Long id, @Valid @RequestBody MobileMoneyProviderRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Provider updated", payrollService.updateMobileMoneyProvider(id, req)));
    }

    @PatchMapping("/mobile-money-providers/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggleMobileMoneyProvider(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Toggled", payrollService.toggleMobileMoneyProvider(id)));
    }

    @DeleteMapping("/mobile-money-providers/{id}")
    public ResponseEntity<ApiResponse<?>> deleteMobileMoneyProvider(@PathVariable Long id) {
        payrollService.deleteMobileMoneyProvider(id);
        return ResponseEntity.ok(ApiResponse.ok("Provider deleted"));
    }

    // ── Payment Types ─────────────────────────────────────────────────────────

    @GetMapping("/payment-types")
    public ResponseEntity<ApiResponse<?>> getPaymentTypes() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllPaymentTypes()));
    }

    @PostMapping("/payment-types")
    public ResponseEntity<ApiResponse<?>> createPaymentType(@Valid @RequestBody PaymentTypeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payment type added", payrollService.createPaymentType(req)));
    }

    @PutMapping("/payment-types/{id}")
    public ResponseEntity<ApiResponse<?>> updatePaymentType(@PathVariable Long id, @Valid @RequestBody PaymentTypeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Payment type updated", payrollService.updatePaymentType(id, req)));
    }

    @PatchMapping("/payment-types/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> togglePaymentType(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Toggled", payrollService.togglePaymentType(id)));
    }

    @DeleteMapping("/payment-types/{id}")
    public ResponseEntity<ApiResponse<?>> deletePaymentType(@PathVariable Long id) {
        payrollService.deletePaymentType(id);
        return ResponseEntity.ok(ApiResponse.ok("Payment type deleted"));
    }

    // ── Staff Payment Details (Finance-only bank/mobile-money info) ─────────────

    @GetMapping("/staff-payment-details")
    public ResponseEntity<ApiResponse<?>> getAllStaffPaymentDetails() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getAllStaffPaymentDetails()));
    }

    @GetMapping("/staff-payment-details/{staffId}")
    public ResponseEntity<ApiResponse<?>> getStaffPaymentDetails(@PathVariable String staffId) {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getStaffPaymentDetails(staffId).orElse(null)));
    }

    @PutMapping("/staff-payment-details")
    public ResponseEntity<ApiResponse<?>> saveStaffPaymentDetails(@Valid @RequestBody StaffPaymentDetailsRequest req) {
        permissionResolver.requirePermission("PAYROLL_STAFF_PAYMENT_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Payment details saved", payrollService.saveStaffPaymentDetails(req)));
    }

    // ── Self-service (any authenticated staff, scoped server-side to their own data) ────

    @GetMapping("/my-payslips")
    public ResponseEntity<ApiResponse<?>> getMyPayslips() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getMyPayslips()));
    }

    // ── Payroll Makers (who can generate/submit payroll) ─────────────────────────

    @GetMapping("/makers")
    public ResponseEntity<ApiResponse<?>> getMakers() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getMakers()));
    }

    @PostMapping("/makers")
    public ResponseEntity<ApiResponse<?>> addMaker(@Valid @RequestBody PayrollMakerRequest req) {
        permissionResolver.requirePermission("PAYROLL_WORKFLOW_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Maker added", payrollService.addMaker(req)));
    }

    @DeleteMapping("/makers/{uuid}")
    public ResponseEntity<ApiResponse<?>> removeMaker(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("PAYROLL_WORKFLOW_MANAGE");
        payrollService.removeMaker(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Maker removed"));
    }

    // ── Payroll Approval Workflow Setup ──────────────────────────────────────────

    @GetMapping("/workflow-steps")
    public ResponseEntity<ApiResponse<?>> getWorkflowSteps() {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getWorkflowSteps()));
    }

    @PostMapping("/workflow-steps")
    public ResponseEntity<ApiResponse<?>> createWorkflowStep(@Valid @RequestBody PayrollWorkflowStepRequest req) {
        permissionResolver.requirePermission("PAYROLL_WORKFLOW_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Workflow step added", payrollService.createWorkflowStep(req)));
    }

    @PutMapping("/workflow-steps/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateWorkflowStep(@PathVariable UUID uuid, @Valid @RequestBody PayrollWorkflowStepRequest req) {
        permissionResolver.requirePermission("PAYROLL_WORKFLOW_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Workflow step updated", payrollService.updateWorkflowStep(uuid, req)));
    }

    @DeleteMapping("/workflow-steps/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteWorkflowStep(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("PAYROLL_WORKFLOW_MANAGE");
        payrollService.deleteWorkflowStep(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Workflow step deleted"));
    }

    @PatchMapping("/workflow-steps/reorder")
    public ResponseEntity<ApiResponse<?>> reorderWorkflowSteps(@Valid @RequestBody WorkflowStepReorderRequest req) {
        permissionResolver.requirePermission("PAYROLL_WORKFLOW_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Workflow reordered", payrollService.reorderWorkflowSteps(req)));
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

    @GetMapping("/runs/{id}/approvals")
    public ResponseEntity<ApiResponse<?>> getRunApprovals(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(payrollService.getRunApprovals(id)));
    }

    @PostMapping("/runs/process")
    public ResponseEntity<ApiResponse<?>> processPayroll(
            @RequestParam int year,
            @RequestParam int monthIndex,
            @RequestParam String monthLabel) {
        permissionResolver.requirePermission("PAYROLL_RUN");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payroll processed", payrollService.processPayroll(year, monthIndex, monthLabel)));
    }

    @PostMapping("/runs/{id}/submit")
    public ResponseEntity<ApiResponse<?>> submitRun(@PathVariable Long id) {
        permissionResolver.requirePermission("PAYROLL_RUN");
        return ResponseEntity.ok(ApiResponse.ok("Submitted for approval", payrollService.submitRun(id)));
    }

    @PostMapping("/runs/{id}/decision")
    public ResponseEntity<ApiResponse<?>> decideRun(@PathVariable Long id, @Valid @RequestBody PayrollDecisionRequest req) {
        permissionResolver.requirePermission("PAYROLL_APPROVE");
        return ResponseEntity.ok(ApiResponse.ok("Decision recorded", payrollService.decideStep(id, req)));
    }

    @PostMapping("/runs/{id}/send-to-bank")
    public ResponseEntity<ApiResponse<?>> sendToBank(@PathVariable Long id) {
        permissionResolver.requirePermission("PAYROLL_SEND_TO_BANK");
        return ResponseEntity.ok(ApiResponse.ok("Marked as sent to bank", payrollService.markSentToBank(id)));
    }
}
