package com.bloom.bloomschool.school.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.school.dto.*;
import com.bloom.bloomschool.school.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;
    private final PermissionResolver permissionResolver;

    // ── School Info ──────────────────────────────────────────────────────────
    // GET deliberately open — usePrintDocument.ts (letterhead: name/logo/address) is used by every
    // portal's receipts/statements, and none of this is sensitive.

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> getInfo() {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getSchoolInfo()));
    }

    @PutMapping("/info")
    public ResponseEntity<ApiResponse<?>> saveInfo(@Valid @RequestBody SchoolInfoRequest req) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        return ResponseEntity.ok(ApiResponse.ok("School info saved", schoolService.saveSchoolInfo(req)));
    }

    // ── Grade Levels ─────────────────────────────────────────────────────────
    // GET deliberately open — needed broadly (admissions/enrollment forms, report filters).

    @GetMapping("/grade-levels")
    public ResponseEntity<ApiResponse<?>> getGradeLevels() {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getAllGradeLevels()));
    }

    @PostMapping("/grade-levels")
    public ResponseEntity<ApiResponse<?>> createGradeLevel(@Valid @RequestBody GradeLevelRequest req) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Grade level created", schoolService.createGradeLevel(req)));
    }

    @PutMapping("/grade-levels/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateGradeLevel(@PathVariable UUID uuid, @Valid @RequestBody GradeLevelRequest req) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        return ResponseEntity.ok(ApiResponse.ok("Grade level updated", schoolService.updateGradeLevel(uuid, req)));
    }

    @PatchMapping("/grade-levels/{uuid}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleGradeLevel(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        schoolService.toggleGradeLevelStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled"));
    }

    @DeleteMapping("/grade-levels/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteGradeLevel(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        schoolService.deleteGradeLevel(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Grade level deleted"));
    }

    // ── Departments ──────────────────────────────────────────────────────────

    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<?>> getDepartments() {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getAllDepartments()));
    }

    @PostMapping("/departments")
    public ResponseEntity<ApiResponse<?>> createDepartment(@Valid @RequestBody DepartmentRequest req) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Department created", schoolService.createDepartment(req)));
    }

    @PutMapping("/departments/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateDepartment(@PathVariable UUID uuid, @Valid @RequestBody DepartmentRequest req) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        return ResponseEntity.ok(ApiResponse.ok("Department updated", schoolService.updateDepartment(uuid, req)));
    }

    @PatchMapping("/departments/{uuid}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleDepartment(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        schoolService.toggleDepartmentStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled"));
    }

    @DeleteMapping("/departments/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteDepartment(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        schoolService.deleteDepartment(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Department deleted"));
    }

    // ── Branches ─────────────────────────────────────────────────────────────

    @GetMapping("/branches")
    public ResponseEntity<ApiResponse<?>> getBranches() {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getAllBranches()));
    }

    @GetMapping("/branches/{uuid}")
    public ResponseEntity<ApiResponse<?>> getBranch(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getBranch(uuid)));
    }

    @PostMapping("/branches")
    public ResponseEntity<ApiResponse<?>> createBranch(@Valid @RequestBody BranchRequest req) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Branch created", schoolService.createBranch(req)));
    }

    @PutMapping("/branches/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateBranch(@PathVariable UUID uuid, @Valid @RequestBody BranchRequest req) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        return ResponseEntity.ok(ApiResponse.ok("Branch updated", schoolService.updateBranch(uuid, req)));
    }

    @PatchMapping("/branches/{uuid}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleBranch(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        schoolService.toggleBranchStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled"));
    }

    @DeleteMapping("/branches/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteBranch(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("SCHOOL_SETUP");
        schoolService.deleteBranch(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Branch deleted"));
    }

    // ── School Bank Accounts (payroll disbursement) ─────────────────────────────

    @GetMapping("/bank-accounts")
    public ResponseEntity<ApiResponse<?>> getBankAccounts() {
        permissionResolver.requirePermission("PAYROLL_STAFF_PAYMENT_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getAllBankAccounts()));
    }

    @PostMapping("/bank-accounts")
    public ResponseEntity<ApiResponse<?>> createBankAccount(@Valid @RequestBody SchoolBankAccountRequest req) {
        permissionResolver.requirePermission("PAYROLL_STAFF_PAYMENT_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Bank account added", schoolService.createBankAccount(req)));
    }

    @PutMapping("/bank-accounts/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateBankAccount(@PathVariable UUID uuid, @Valid @RequestBody SchoolBankAccountRequest req) {
        permissionResolver.requirePermission("PAYROLL_STAFF_PAYMENT_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Bank account updated", schoolService.updateBankAccount(uuid, req)));
    }

    @PatchMapping("/bank-accounts/{uuid}/toggle")
    public ResponseEntity<ApiResponse<?>> toggleBankAccount(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("PAYROLL_STAFF_PAYMENT_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Toggled", schoolService.toggleBankAccount(uuid)));
    }

    @PatchMapping("/bank-accounts/{uuid}/use-for-payroll")
    public ResponseEntity<ApiResponse<?>> setUseForPayroll(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("PAYROLL_STAFF_PAYMENT_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Payroll debit account set", schoolService.setUseForPayroll(uuid)));
    }

    @DeleteMapping("/bank-accounts/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteBankAccount(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("PAYROLL_STAFF_PAYMENT_MANAGE");
        schoolService.deleteBankAccount(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Bank account deleted"));
    }
}
