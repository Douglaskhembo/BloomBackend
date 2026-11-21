package com.bloom.bloomschool.feestructure.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.feestructure.dto.FeeStructureDecisionRequest;
import com.bloom.bloomschool.feestructure.dto.FeeStructureRequestDto;
import com.bloom.bloomschool.feestructure.dto.FeeStructureResponseDto;
import com.bloom.bloomschool.feestructure.entity.FeeStructureEntity;
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

    @GetMapping("/get-all-feeStructures")
    public ResponseEntity<ApiResponse<List<FeeStructureResponseDto>>> getStructures(){
        return ResponseEntity.ok(ApiResponse.ok(service.getAllFeeStructures()));
    }

    @GetMapping("/get-fee-structure/{uuid}")
    public ResponseEntity<ApiResponse<FeeStructureResponseDto>> getFeeStructure(@PathVariable UUID uuid){
        return ResponseEntity.ok(ApiResponse.ok(service.getFeeStructure(uuid)));
    }

    @PostMapping("/feeStructure/submit")
    public ResponseEntity<ApiResponse<?>> createFeeStructure(@RequestBody FeeStructureRequestDto body){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Submitted for approval", service.submitForApproval(body)));
    }

    @DeleteMapping("/delete-FeeStructure/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteFeeStructure(@PathVariable UUID uuid){
        service.deleteFeeStructure(uuid);

        return ResponseEntity.ok(ApiResponse.ok("Fee structure deleted"));
    }

    @GetMapping("/structure/audit")
    public ResponseEntity<ApiResponse<List<FeeStructureResponseDto>>> getFeeStructureDescOrder(){
        return ResponseEntity.ok(ApiResponse.ok(service.getAllFeeStructuresDescOrder()));
    }

    @PostMapping("/structure/draft")
    public ResponseEntity<ApiResponse<FeeStructureEntity>> saveDraft(@RequestBody FeeStructureRequestDto body){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Draft Saved", service.saveDraft(body)));
    }

    @PatchMapping("/structure/approve/{feeStructureUuid}")
    public ResponseEntity<ApiResponse<?>> approveFeeStructure(@PathVariable UUID feeStructureUuid){
        service.approveFeeStructure(feeStructureUuid);
        return ResponseEntity.ok(ApiResponse.ok("Fee structure approved"));
    }

    @PatchMapping("/structure/reject/{feeStructureUuid}")
    public ResponseEntity<ApiResponse<?>> rejectFeeStructure(@PathVariable UUID feeStructureUuid,
                                                             @RequestBody FeeStructureDecisionRequest request){
        if(request == null){
            throw new IllegalArgumentException("Reject reason must be valid");
        }
        service.rejectFeeStructure(feeStructureUuid, request);
        return ResponseEntity.ok(ApiResponse.ok("Fee structure rejected"));
    }
}

