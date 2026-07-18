package com.bloom.bloomschool.school.controller;

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

    // ── School Info ──────────────────────────────────────────────────────────

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> getInfo() {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getSchoolInfo()));
    }

    @PutMapping("/info")
    public ResponseEntity<ApiResponse<?>> saveInfo(@Valid @RequestBody SchoolInfoRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("School info saved", schoolService.saveSchoolInfo(req)));
    }

    // ── Grade Levels ─────────────────────────────────────────────────────────

    @GetMapping("/grade-levels")
    public ResponseEntity<ApiResponse<?>> getGradeLevels() {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getAllGradeLevels()));
    }

    @PostMapping("/grade-levels")
    public ResponseEntity<ApiResponse<?>> createGradeLevel(@Valid @RequestBody GradeLevelRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Grade level created", schoolService.createGradeLevel(req)));
    }

    @PutMapping("/grade-levels/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateGradeLevel(@PathVariable UUID uuid, @Valid @RequestBody GradeLevelRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Grade level updated", schoolService.updateGradeLevel(uuid, req)));
    }

    @PatchMapping("/grade-levels/{uuid}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleGradeLevel(@PathVariable UUID uuid) {
        schoolService.toggleGradeLevelStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled"));
    }

    @DeleteMapping("/grade-levels/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteGradeLevel(@PathVariable UUID uuid) {
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Department created", schoolService.createDepartment(req)));
    }

    @PutMapping("/departments/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateDepartment(@PathVariable UUID uuid, @Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Department updated", schoolService.updateDepartment(uuid, req)));
    }

    @PatchMapping("/departments/{uuid}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleDepartment(@PathVariable UUID uuid) {
        schoolService.toggleDepartmentStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled"));
    }

    @DeleteMapping("/departments/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteDepartment(@PathVariable UUID uuid) {
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Branch created", schoolService.createBranch(req)));
    }

    @PutMapping("/branches/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateBranch(@PathVariable UUID uuid, @Valid @RequestBody BranchRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Branch updated", schoolService.updateBranch(uuid, req)));
    }

    @PatchMapping("/branches/{uuid}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleBranch(@PathVariable UUID uuid) {
        schoolService.toggleBranchStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled"));
    }

    @DeleteMapping("/branches/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteBranch(@PathVariable UUID uuid) {
        schoolService.deleteBranch(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Branch deleted"));
    }
}
