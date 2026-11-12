package com.bloom.bloomschool.feestructure.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.feestructure.dto.FeePaymentRequestDto;
import com.bloom.bloomschool.feestructure.dto.FeePaymentResponseDto;
import com.bloom.bloomschool.feestructure.service.FeePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feePayment")
@RequiredArgsConstructor
public class FeePaymentController {
    private final FeePaymentService service;

    @GetMapping("/get-payments")
    public ResponseEntity<ApiResponse<List<FeePaymentResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/payments/balance/{studentUuid}")
    public ResponseEntity<ApiResponse<Double>> getFeeBalance(@PathVariable UUID studentUuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.getBalance(studentUuid)));
    }

    @PostMapping("/create/payment")
    public ResponseEntity<ApiResponse<?>> createFeePayment(@RequestBody FeePaymentRequestDto body) {
        service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Payment created"));
    }

    @DeleteMapping("/deletePayment/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteFeePayment(@PathVariable UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Payment deleted"));
    }

    @GetMapping("/payments/pending-verification/{studentUuid}")
    public ResponseEntity<ApiResponse<List<FeePaymentResponseDto>>> getPendingPayments(@PathVariable UUID studentUuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.getPendingVerifications(studentUuid)));
    }

    @PatchMapping("/payment/verify-payment/{uuid}")
    public ResponseEntity<ApiResponse<?>> verifyFeePayment(@PathVariable UUID uuid) {
        service.verifyPayment(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Payment verified"));
    }

    @PatchMapping("/payment/reject-payment/{uuid}")
    public ResponseEntity<ApiResponse<?>> rejectFeePayment(@PathVariable UUID uuid) {
        service.rejectPayment(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Payment rejected"));
    }
}