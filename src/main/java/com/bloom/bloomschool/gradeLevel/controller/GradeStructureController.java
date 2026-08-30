package com.bloom.bloomschool.gradeLevel.controller;

import com.bloom.bloomschool.gradeLevel.dto.GradeStructureDto;
import com.bloom.bloomschool.gradeLevel.service.GradeStructureService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
    public ResponseEntity<List<GradeStructureDto>> get_all(){return ResponseEntity.ok(service.get_all());}

    @GetMapping("/grade-structure/{uuid}")
    public ResponseEntity<GradeStructureDto> get(@PathVariable UUID uuid) throws BadRequestException {
        return ResponseEntity.ok(service.get_structure(uuid));
    }

    @PostMapping("createGrade-structure")
    public ResponseEntity<?> create(@RequestBody GradeStructureDto req){
        service.createGradeStructure(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("updateGrade-structure/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody GradeStructureDto req){
        service.updateGradeStructure(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    @DeleteMapping("deleteGrade-structure/{uuid}")
    public ResponseEntity<?> deleteGrade(@PathVariable UUID uuid){
        service.deleteGradeStructure(uuid);
        return ResponseEntity.noContent().build();
    }
}
