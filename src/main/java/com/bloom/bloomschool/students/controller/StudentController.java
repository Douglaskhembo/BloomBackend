package com.bloom.bloomschool.students.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.students.dto.AdmissionRequest;
import com.bloom.bloomschool.students.dto.StudentRequest;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.service.StudentService;
import com.bloom.bloomschool.students.util.Stage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final PermissionResolver permissionResolver;

    // ── Students ─────────────────────────────────────────────────────────────

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<?>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getAll(search)));
    }

    @GetMapping("/students/{uuid}")
    public ResponseEntity<ApiResponse<?>> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getByUuid(uuid)));
    }

    @PostMapping("/students")
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody StudentRequest req) {
        permissionResolver.requirePermission("STUDENT_CREATE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Student created", studentService.create(req)));
    }

    @PutMapping("/students/{uuid}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable UUID uuid, @Valid @RequestBody StudentRequest req) {
        permissionResolver.requirePermission("STUDENT_EDIT");
        return ResponseEntity.ok(ApiResponse.ok("Student updated", studentService.update(uuid, req)));
    }

    @PatchMapping("/students/{uuid}/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable UUID uuid, @RequestParam Student.Status status) {
        permissionResolver.requirePermission("STUDENT_EDIT");
        return ResponseEntity.ok(ApiResponse.ok("Status updated", studentService.updateStatus(uuid, status)));
    }

    @DeleteMapping("/students/{uuid}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("STUDENT_DELETE");
        studentService.delete(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Student deleted"));
    }

    /** Active children linked to the given PARENT-role user — used by the parent portal. */
    @GetMapping("/students/my-children")
    public ResponseEntity<ApiResponse<?>> getMyChildren(@RequestParam UUID parentUserUuid) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getMyChildren(parentUserUuid)));
    }

    @PatchMapping("/students/{uuid}/link-parent")
    public ResponseEntity<ApiResponse<?>> linkParent(@PathVariable UUID uuid, @RequestParam UUID parentUserUuid) {
        permissionResolver.requirePermission("STUDENT_EDIT");
        return ResponseEntity.ok(ApiResponse.ok("Parent linked", studentService.linkParent(uuid, parentUserUuid)));
    }

    // ── Admissions ───────────────────────────────────────────────────────────

    @GetMapping("/admissions")
    public ResponseEntity<ApiResponse<?>> getAllAdmissions() {
        permissionResolver.requirePermission("ADMISSION_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(studentService.getAllAdmissions()));
    }

    @GetMapping("/admissions/{uuid}")
    public ResponseEntity<ApiResponse<?>> getAdmission(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("ADMISSION_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(studentService.getAdmissionByUuid(uuid)));
    }

    @PostMapping("/admissions")
    public ResponseEntity<ApiResponse<?>> createAdmission(@Valid @RequestBody AdmissionRequest req) {
        permissionResolver.requirePermission("ADMISSION_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Application submitted", studentService.createAdmission(req)));
    }

    @PatchMapping("/admissions/{uuid}/stage")
    public ResponseEntity<ApiResponse<?>> updateStage(@PathVariable UUID uuid, @RequestParam Stage stage,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate joinDate) {
        permissionResolver.requirePermission("ADMISSION_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Stage updated", studentService.updateAdmissionStage(uuid, stage, joinDate)));
    }

    @PutMapping("/admissions/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateAdmission(@PathVariable UUID uuid, @Valid @RequestBody AdmissionRequest req) {
        permissionResolver.requirePermission("ADMISSION_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Application updated", studentService.updateAdmission(uuid, req)));
    }

    @DeleteMapping("/admissions/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteAdmission(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("ADMISSION_MANAGE");
        studentService.deleteAdmission(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Application deleted"));
    }
}
