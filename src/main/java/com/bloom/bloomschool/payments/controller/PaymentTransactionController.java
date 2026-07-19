package com.bloom.bloomschool.payments.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.payments.dto.ManualReconcileRequest;
import com.bloom.bloomschool.payments.entity.PaymentTransaction;
import com.bloom.bloomschool.payments.repository.PaymentTransactionRepository;
import com.bloom.bloomschool.payments.service.PaymentReconciliationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Authenticated admin/staff visibility into gateway transactions, plus manual fix-up for UNMATCHED ones. */
@RestController
@RequestMapping("/payments/transactions")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionRepository txRepo;
    private final PaymentReconciliationService reconciliationService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(@RequestParam(required = false) PaymentTransaction.Status status) {
        if (status != null) return ResponseEntity.ok(ApiResponse.ok(txRepo.findByStatusOrderByReceivedAtDesc(status)));
        return ResponseEntity.ok(ApiResponse.ok(txRepo.findAllOrderByReceivedAtDesc()));
    }

    @GetMapping("/unmatched")
    public ResponseEntity<ApiResponse<?>> getUnmatched() {
        return ResponseEntity.ok(ApiResponse.ok(txRepo.findUnmatched()));
    }

    @PatchMapping("/{id}/reconcile")
    public ResponseEntity<ApiResponse<?>> reconcile(@PathVariable Long id, @Valid @RequestBody ManualReconcileRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Transaction reconciled", reconciliationService.manualReconcile(id, req.getAdmissionNumber())));
    }
}
