package com.bloom.bloomschool.feestructure.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.feestructure.dto.StudentFeeChargeRequestDto;
import com.bloom.bloomschool.feestructure.dto.StudentFeeChargeResponseDto;
import com.bloom.bloomschool.feestructure.service.StudentFeeChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/studentFeeCharge")
@RequiredArgsConstructor
public class StudentFeeChargeController {
    private final StudentFeeChargeService service;

    @GetMapping("/get-all-student-fee-charges")
    public ResponseEntity<ApiResponse<List<StudentFeeChargeResponseDto>>> getAllStudentFeeCharges(){
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/get-student-fee-charge/{uuid}")
    public ResponseEntity<ApiResponse<StudentFeeChargeResponseDto>> getStudentFeeCharge(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.getFeeCharge(uuid)));
    }

    @PostMapping("/create-student-fee-charge")
    public ResponseEntity<ApiResponse<?>> createStudentFeeCharge(@RequestBody StudentFeeChargeRequestDto body){
        service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Student fee charge created"));
    }

    @PutMapping("/update-student-fee-charge/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateStudentFeeCharge(@PathVariable UUID uuid, @RequestBody StudentFeeChargeRequestDto body) {
        service.update(uuid, body);
        return ResponseEntity.ok(ApiResponse.ok("Student fee charge updated"));
    }

    @DeleteMapping("/delete-student-fee-charge/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteStudentFeeCharge(@PathVariable UUID uuid){
        service.delete(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Student fee charge deleted"));
    }
}