package com.bloom.bloomschool.bills.controller;

import com.bloom.bloomschool.bills.dto.BillRequest;
import com.bloom.bloomschool.bills.service.BillService;
import com.bloom.bloomschool.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(billService.getAll(search)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(billService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody BillRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Bill created", billService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @Valid @RequestBody BillRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Bill updated", billService.update(id, req)));
    }

    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<ApiResponse<?>> markPaid(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Bill marked as paid", billService.markPaid(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        billService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Bill deleted"));
    }
}
