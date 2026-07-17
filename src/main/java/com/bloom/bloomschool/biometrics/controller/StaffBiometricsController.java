package com.bloom.bloomschool.biometrics.controller;

import com.bloom.bloomschool.auth.utils.ApiResponse;
import com.bloom.bloomschool.auth.utils.GenericResponse;
import com.bloom.bloomschool.biometrics.dto.request.BioCaptureRequest;
import com.bloom.bloomschool.biometrics.dto.request.BioEnrollRequest;
import com.bloom.bloomschool.biometrics.entity.StaffBioData;
import com.bloom.bloomschool.biometrics.service.StaffBiometricsService;
import com.bloom.bloomschool.biometrics.util.EnrollmentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/biometrics/staff")
@RequiredArgsConstructor
public class StaffBiometricsController {

    private final StaffBiometricsService service;
    private final GenericResponse genericResponse;

    @PostMapping("/{staffUuid}/enroll")
    public ResponseEntity<ApiResponse<Object>> enroll(@PathVariable UUID staffUuid,
                                                       @Valid @RequestBody BioEnrollRequest req) {
        return genericResponse.response(service.enroll(staffUuid, req), HttpStatus.CREATED);
    }

    @GetMapping("/{staffUuid}")
    public ResponseEntity<ApiResponse<Object>> getBioData(@PathVariable UUID staffUuid) {
        return genericResponse.response(service.getBioData(staffUuid), HttpStatus.OK);
    }

    @PatchMapping("/{bioUuid}/status")
    public ResponseEntity<ApiResponse<Object>> updateStatus(@PathVariable UUID bioUuid,
                                                             @RequestParam EnrollmentStatus status) {
        return genericResponse.response(service.updateStatus(bioUuid, status), HttpStatus.OK);
    }

    /** Endpoint called by the biometric device on every scan */
    @PostMapping("/capture")
    public ResponseEntity<ApiResponse<Object>> capture(@Valid @RequestBody BioCaptureRequest req) {
        return genericResponse.response(service.capture(req), HttpStatus.OK);
    }

    @GetMapping("/{staffUuid}/attendance")
    public ResponseEntity<ApiResponse<Object>> getAttendance(
            @PathVariable UUID staffUuid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return genericResponse.response(service.getAttendance(staffUuid, from, to), HttpStatus.OK);
    }

    @GetMapping("/attendance/daily")
    public ResponseEntity<ApiResponse<Object>> getDailyAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return genericResponse.response(
                service.getDailyAttendance(date != null ? date : LocalDate.now()), HttpStatus.OK);
    }
}
