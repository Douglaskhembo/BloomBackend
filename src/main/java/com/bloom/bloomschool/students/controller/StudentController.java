package com.bloom.bloomschool.students.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.students.dto.AdmissionRequest;
import com.bloom.bloomschool.students.dto.StudentRequest;
import com.bloom.bloomschool.students.entity.Admission;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.service.StudentService;
import com.bloom.bloomschool.students.util.Stage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // ── Students ─────────────────────────────────────────────────────────────

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<?>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getAll(search)));
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getById(id)));
    }

    @PostMapping("/students")
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody StudentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Student created", studentService.create(req)));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @Valid @RequestBody StudentRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Student updated", studentService.update(id, req)));
    }

    @PatchMapping("/students/{id}/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable Long id, @RequestParam Student.Status status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", studentService.updateStatus(id, status)));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Student deleted"));
    }

    // ── Admissions ───────────────────────────────────────────────────────────

    @GetMapping("/admissions")
    public ResponseEntity<ApiResponse<?>> getAllAdmissions() {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getAllAdmissions()));
    }

    @GetMapping("/admissions/{id}")
    public ResponseEntity<ApiResponse<?>> getAdmission(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getAdmissionById(id)));
    }

    @PostMapping("/admissions")
    public ResponseEntity<ApiResponse<?>> createAdmission(@Valid @RequestBody AdmissionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Application submitted", studentService.createAdmission(req)));
    }

    @PatchMapping("/admissions/{id}/stage")
    public ResponseEntity<ApiResponse<?>> updateStage(@PathVariable Long id, @RequestParam Stage stage) {
        return ResponseEntity.ok(ApiResponse.ok("Stage updated", studentService.updateAdmissionStage(id, stage)));
    }

    @PutMapping("/admissions/{id}")
    public ResponseEntity<ApiResponse<?>> updateAdmission(@PathVariable Long id, @Valid @RequestBody AdmissionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Application updated", studentService.updateAdmission(id, req)));
    }

    @DeleteMapping("/admissions/{id}")
    public ResponseEntity<ApiResponse<?>> deleteAdmission(@PathVariable Long id) {
        studentService.deleteAdmission(id);
        return ResponseEntity.ok(ApiResponse.ok("Application deleted"));
    }
}
