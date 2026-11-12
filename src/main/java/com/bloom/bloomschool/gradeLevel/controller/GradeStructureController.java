package com.bloom.bloomschool.gradeLevel.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.gradeLevel.dto.GradeStructureRequestDto;
import com.bloom.bloomschool.gradeLevel.dto.GradeStructureResponseDto;
import com.bloom.bloomschool.gradeLevel.service.GradeStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/grade-structure")
@RequiredArgsConstructor
public class GradeStructureController {

    private final GradeStructureService service;

    @GetMapping("/get-all-grade-structures")
    public ResponseEntity<ApiResponse<List<GradeStructureResponseDto>>> getAllGradeStructures(){
        return ResponseEntity.ok(ApiResponse.ok(service.get_all()));
    }

    @GetMapping("/get-grade-structure/{uuid}")
    public ResponseEntity<ApiResponse<GradeStructureResponseDto>> getGradeStructure(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.get_structure(uuid)));
    }

    @PostMapping("/create-grade-structure")
    public ResponseEntity<ApiResponse<?>> createGradeStructure(@RequestBody GradeStructureRequestDto req){
        service.createGradeStructure(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Grade structure created"));
    }

    @PutMapping("/update-grade-structure/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateGradeStructure(@PathVariable UUID uuid, @RequestBody GradeStructureRequestDto req){
        service.updateGradeStructure(uuid, req);
        return ResponseEntity.ok(ApiResponse.ok("Grade structure updated"));
    }

    @DeleteMapping("/delete-grade-structure/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteGradeStructure(@PathVariable UUID uuid){
        service.deleteGradeStructure(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Grade structure deleted"));
    }
}