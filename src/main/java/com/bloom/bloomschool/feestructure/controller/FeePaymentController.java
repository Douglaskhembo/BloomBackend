package com.bloom.bloomschool.feestructure.controller;

import com.bloom.bloomschool.feestructure.dto.FeePaymentDto;
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

    @GetMapping("/get-all")
    public ResponseEntity<List<FeePaymentDto>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get/{uuid}")
    public ResponseEntity<FeePaymentDto> getFeeItem(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getPayment(uuid));
    }

    @GetMapping("/get-balance/{uuid}")
    public ResponseEntity<Double> getFeeBalance(@PathVariable UUID uuid){
        return ResponseEntity.ok(service.getBalance(uuid));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createFeeItem(@RequestBody FeePaymentDto body){
        service.create(body);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @DeleteMapping("/deleteAdmission/{uuid}")
    public ResponseEntity<?> deleteFeeItem(@PathVariable UUID uuid){
        service.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}

