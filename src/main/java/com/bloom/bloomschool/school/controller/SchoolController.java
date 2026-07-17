package com.bloom.bloomschool.school.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.school.dto.*;
import com.bloom.bloomschool.school.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/grade-levels/{id}")
    public ResponseEntity<ApiResponse<?>> updateGradeLevel(@PathVariable Long id, @Valid @RequestBody GradeLevelRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Grade level updated", schoolService.updateGradeLevel(id, req)));
    }

    @PatchMapping("/grade-levels/{id}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleGradeLevel(@PathVariable Long id) {
        schoolService.toggleGradeLevelStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled"));
    }

    @DeleteMapping("/grade-levels/{id}")
    public ResponseEntity<ApiResponse<?>> deleteGradeLevel(@PathVariable Long id) {
        schoolService.deleteGradeLevel(id);
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

    @PutMapping("/departments/{id}")
    public ResponseEntity<ApiResponse<?>> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Department updated", schoolService.updateDepartment(id, req)));
    }

    @PatchMapping("/departments/{id}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleDepartment(@PathVariable Long id) {
        schoolService.toggleDepartmentStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled"));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<ApiResponse<?>> deleteDepartment(@PathVariable Long id) {
        schoolService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.ok("Department deleted"));
    }

    // ── Branches ─────────────────────────────────────────────────────────────

    @GetMapping("/branches")
    public ResponseEntity<ApiResponse<?>> getBranches() {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getAllBranches()));
    }

    @GetMapping("/branches/{id}")
    public ResponseEntity<ApiResponse<?>> getBranch(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getBranch(id)));
    }

    @PostMapping("/branches")
    public ResponseEntity<ApiResponse<?>> createBranch(@Valid @RequestBody BranchRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Branch created", schoolService.createBranch(req)));
    }

    @PutMapping("/branches/{id}")
    public ResponseEntity<ApiResponse<?>> updateBranch(@PathVariable Long id, @Valid @RequestBody BranchRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Branch updated", schoolService.updateBranch(id, req)));
    }

    @PatchMapping("/branches/{id}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleBranch(@PathVariable Long id) {
        schoolService.toggleBranchStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled"));
    }

    @DeleteMapping("/branches/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBranch(@PathVariable Long id) {
        schoolService.deleteBranch(id);
        return ResponseEntity.ok(ApiResponse.ok("Branch deleted"));
    }
}
