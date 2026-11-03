package com.bloom.bloomschool.gradeLevel.controller;

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
@RequestMapping("/api/v1/gradingStructure")
@RequiredArgsConstructor
public class GradeStructureController {

    private final GradeStructureService service;

    @GetMapping("/all-grading-structures")
    public ResponseEntity<List<GradeStructureResponseDto>> get_all(){
        return ResponseEntity.ok(service.get_all());
    }

    @GetMapping("/grade-structure/{uuid}")
    public ResponseEntity<GradeStructureResponseDto> get(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.get_structure(uuid));
    }

    @PostMapping("/createGrade-structure")
    public ResponseEntity<?> create(@RequestBody GradeStructureRequestDto req){
        service.createGradeStructure(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/updateGrade-structure/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody GradeStructureRequestDto req){
        service.updateGradeStructure(uuid, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteGrade-structure/{uuid}")
    public ResponseEntity<?> deleteGrade(@PathVariable UUID uuid){
        service.deleteGradeStructure(uuid);
        return ResponseEntity.noContent().build();
    }
}