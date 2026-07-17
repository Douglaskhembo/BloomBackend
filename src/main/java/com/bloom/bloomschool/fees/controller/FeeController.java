package com.bloom.bloomschool.fees.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.fees.dto.FeeItemRequest;
import com.bloom.bloomschool.fees.dto.FeePaymentRequest;
import com.bloom.bloomschool.fees.service.FeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;

    // ── Fee Items ─────────────────────────────────────────────────────────────

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<?>> getFeeItems(@RequestParam(required = false) String grade) {
        if (grade != null) return ResponseEntity.ok(ApiResponse.ok(feeService.getFeeItemsByGrade(grade)));
        return ResponseEntity.ok(ApiResponse.ok(feeService.getAllFeeItems()));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<?>> createFeeItem(@Valid @RequestBody FeeItemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Fee item created", feeService.createFeeItem(req)));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<?>> updateFeeItem(@PathVariable Long id, @Valid @RequestBody FeeItemRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Fee item updated", feeService.updateFeeItem(id, req)));
    }

    @PatchMapping("/items/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggleFeeItem(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Toggled", feeService.toggleFeeItem(id)));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<?>> deleteFeeItem(@PathVariable Long id) {
        feeService.deleteFeeItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Fee item deleted"));
    }

    // ── Fee Payments ──────────────────────────────────────────────────────────

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<?>> getPayments(@RequestParam(required = false) String studentId) {
        if (studentId != null) return ResponseEntity.ok(ApiResponse.ok(feeService.getPaymentsByStudent(studentId)));
        return ResponseEntity.ok(ApiResponse.ok(feeService.getAllPayments()));
    }

    @GetMapping("/payments/balance/{studentId}")
    public ResponseEntity<ApiResponse<?>> getBalance(@PathVariable String studentId) {
        return ResponseEntity.ok(ApiResponse.ok(feeService.getTotalPaidByStudent(studentId)));
    }

    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<?>> recordPayment(@Valid @RequestBody FeePaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payment recorded", feeService.recordPayment(req)));
    }

    @DeleteMapping("/payments/{id}")
    public ResponseEntity<ApiResponse<?>> deletePayment(@PathVariable Long id) {
        feeService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.ok("Payment deleted"));
    }
}
