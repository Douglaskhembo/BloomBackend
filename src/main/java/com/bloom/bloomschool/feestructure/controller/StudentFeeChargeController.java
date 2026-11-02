package com.bloom.bloomschool.feestructure.controller;

import com.bloom.bloomschool.feestructure.dto.StudentFeeChargeDto;
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

    @GetMapping("/get-all")
    public ResponseEntity<List<StudentFeeChargeDto>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get/{uuid}")
    public ResponseEntity<StudentFeeChargeDto> getFeeItem(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getFeeCharge(uuid));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createFeeItem(@RequestBody StudentFeeChargeDto body){
        service.create(body);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{uuid}")
    public ResponseEntity<?> updateFeeItem(@PathVariable UUID uuid, @RequestBody StudentFeeChargeDto body) {
        service.update(uuid, body);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteAdmission/{uuid}")
    public ResponseEntity<?> deleteFeeItem(@PathVariable UUID uuid){
        service.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
