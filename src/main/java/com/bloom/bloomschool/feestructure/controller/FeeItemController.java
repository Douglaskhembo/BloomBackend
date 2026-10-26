package com.bloom.bloomschool.feestructure.controller;

import com.bloom.bloomschool.feestructure.dto.FeeItemDto;
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

    @GetMapping("/get-all")
    public ResponseEntity<List<FeeItemDto>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get/{uuid}")
    public ResponseEntity<FeeItemDto> getFeeItem(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getFeeItem(uuid));
    }

    @PostMapping("/create-FeeItem")
    public ResponseEntity<?> createFeeItem(@RequestBody FeeItemDto body){
        service.createFeeItem(body);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update-FeeItem/{uuid}")
    public ResponseEntity<?> updateFeeItem(@PathVariable UUID uuid, @RequestBody FeeItemDto body) {
        service.updateFeeItem(body, uuid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/toggle-status/{uuid}")
    public ResponseEntity<?> toggleStatus(@PathVariable UUID uuid){
        service.toggleFeeItemStatus(uuid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteAdmission-FeeItem/{uuid}")
    public ResponseEntity<?> deleteFeeItem(@PathVariable UUID uuid){
        service.deleteFeeItem(uuid);
        return ResponseEntity.noContent().build();
    }
}