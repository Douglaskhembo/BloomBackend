package com.bloom.bloomschool.student.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.student.dto.AdmissionRequestDto;
import com.bloom.bloomschool.student.dto.AdmissionResponseDto;
import com.bloom.bloomschool.student.enums.ApplicationStage;
import com.bloom.bloomschool.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admissions")
@RequiredArgsConstructor
public class AdmissionsController {
    private final StudentService service;

    @GetMapping("/get-all-admissions")
    public ResponseEntity<ApiResponse<List<AdmissionResponseDto>>> getAllAdmissions() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAllAdmissions()));
    }

    @GetMapping("/get-admission/{uuid}")
    public ResponseEntity<ApiResponse<AdmissionResponseDto>> getAdmission(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.getAdmission(uuid)));
    }

    @PostMapping("/create-admission")
    public ResponseEntity<ApiResponse<?>> createAdmission(@RequestBody AdmissionRequestDto req) {
        service.createAdmission(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Admission created"));
    }

    @PutMapping("/update-admission/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateAdmission(@PathVariable UUID uuid, @RequestBody AdmissionRequestDto req) {
        service.updateAdmission(uuid, req);
        return ResponseEntity.ok(ApiResponse.ok("Admission updated"));
    }

    @DeleteMapping("/delete-admission/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteAdmission(@PathVariable UUID uuid) {
        service.deleteAdmission(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Admission deleted"));
    }

    @PatchMapping("/update-admission-status/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateAdmissionStatus(@PathVariable UUID uuid,
                                                                @RequestParam ApplicationStage stage,
                                                                @RequestParam(required = false) LocalDate joinDate) {
        service.updateAdmissionStage(uuid, stage, joinDate);
        return ResponseEntity.ok(ApiResponse.ok("Admission status updated"));
    }
}