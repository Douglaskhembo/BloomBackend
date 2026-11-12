package com.bloom.bloomschool.feestructure.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.feestructure.dto.FeeStructureRequestDto;
import com.bloom.bloomschool.feestructure.dto.FeeStructureResponseDto;
import com.bloom.bloomschool.feestructure.service.FeeStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feeStructure")
@RequiredArgsConstructor
public class FeeStructureController {
    private final FeeStructureService service;

    @GetMapping("/get-all-FeeStructures")
    public ResponseEntity<ApiResponse<List<FeeStructureResponseDto>>> getAll(){
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/get-fee-structure/{uuid}")
    public ResponseEntity<ApiResponse<FeeStructureResponseDto>> getFeeStructure(@PathVariable UUID uuid){
        return ResponseEntity.ok(ApiResponse.ok(service.getFeeStructure(uuid)));
    }

    @PostMapping("/create-FeeStructure")
    public ResponseEntity<ApiResponse<?>> createFeeStructure(@RequestBody FeeStructureRequestDto body){
        service.createFeeStructure(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Fee structure created"));
    }

    @PutMapping("/update-FeeStructure/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateFeeStructure(@PathVariable UUID uuid, @RequestBody FeeStructureRequestDto body) {
        service.updateFeeStructure(uuid, body);
        return ResponseEntity.ok(ApiResponse.ok("Fee structure updated"));
    }

    @DeleteMapping("/delete-FeeStructure/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteFeeStructure(@PathVariable UUID uuid){
        service.deleteFeeStructure(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Fee structure deleted"));
    }
}