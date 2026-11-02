package com.bloom.bloomschool.feestructure.controller;

import com.bloom.bloomschool.feestructure.dto.FeeStructureDto;
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

    @GetMapping("/get-all")
    public ResponseEntity<List<FeeStructureDto>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get/{uuid}")
    public ResponseEntity<FeeStructureDto> getFeeStructure(@PathVariable UUID uuid){
        return ResponseEntity.ok(service.getFeeStructure(uuid));
    }

    @PostMapping("/create-FeeStructure")
    public ResponseEntity<?> createFeeStructure(@RequestBody FeeStructureDto body){
        service.createFeeStructure(body);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update-FeeStructure/{uuid}")
    public ResponseEntity<?> updateFeeStructure(@PathVariable UUID uuid, @RequestBody FeeStructureDto body) {
        service.updateFeeStructure(uuid, body);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/deleteAdmission-FeeStructure/{uuid}")
    public ResponseEntity<?> deleteFeeStructure(@PathVariable UUID uuid){
        service.deleteFeeStructure(uuid);
        return ResponseEntity.noContent().build();
    }
}
