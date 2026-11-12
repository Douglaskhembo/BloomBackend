package com.bloom.bloomschool.feestructure.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.feestructure.dto.FeeItemRequestDto;
import com.bloom.bloomschool.feestructure.dto.FeeItemResponseDto;
import com.bloom.bloomschool.feestructure.service.FeeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feeItem")
@RequiredArgsConstructor
public class FeeItemController {
    private final FeeItemService service;

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<?>> getFeeItems(@RequestParam(required = false) String grade){
        if(grade != null){
            return ResponseEntity.ok(ApiResponse.ok(service.getFeeItemByGrade(grade)));
        }
        return ResponseEntity.ok(ApiResponse.ok(service.getAllFeeItems()));
    }

    @GetMapping("/get-fee-item/{uuid}")
    public ResponseEntity<ApiResponse<FeeItemResponseDto>> getFeeItem(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.getFeeItem(uuid)));
    }

    @PostMapping("/create-FeeItem")
    public ResponseEntity<ApiResponse<?>> createFeeItem(@RequestBody FeeItemRequestDto body){
        service.createFeeItem(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Fee item created"));
    }

    @PutMapping("/update-FeeItem/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateFeeItem(@PathVariable UUID uuid, @RequestBody FeeItemRequestDto body) {
        service.updateFeeItem(body, uuid);
        return ResponseEntity.ok(ApiResponse.ok("Fee item updated"));
    }

    @PatchMapping("/toggle-status/{uuid}")
    public ResponseEntity<ApiResponse<?>> toggleStatus(@PathVariable UUID uuid){
        service.toggleFeeItemStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Fee item status updated"));
    }

    @DeleteMapping("/deleteAdmission-FeeItem/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteFeeItem(@PathVariable UUID uuid){
        service.deleteFeeItem(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Fee item deleted"));
    }
}