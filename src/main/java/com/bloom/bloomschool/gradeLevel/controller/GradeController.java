package com.bloom.bloomschool.gradeLevel.controller;

import com.bloom.bloomschool.gradeLevel.dto.GradeRequestDto;
import com.bloom.bloomschool.gradeLevel.dto.GradeResponseDto;
import com.bloom.bloomschool.gradeLevel.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/grade")
@RequiredArgsConstructor
public class GradeController {
    private final GradeService service;

    @GetMapping("/allGrades")
    public ResponseEntity<List<GradeResponseDto>> get_all_grades(){
        return ResponseEntity.ok(service.get_all_grades());
    }

    @GetMapping("/grade/{uuid}")
    public ResponseEntity<GradeResponseDto> get_grade(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.get_grade(uuid));
    }

    @PostMapping("/createGrade")
    public ResponseEntity<?> create(@RequestBody GradeRequestDto req){
        System.out.println("Data>>>>>" +req);
        service.createGrade(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/updateGrade/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody GradeRequestDto req){
        service.update(uuid, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/toggleStatus/{uuid}")
    public ResponseEntity<?> toggleStatus(@PathVariable UUID uuid){
        service.toggleStatus(uuid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteGrade/{uuid}")
    public ResponseEntity<?> deleteGrade(@PathVariable UUID uuid){
        service.deleteGrade(uuid);
        return ResponseEntity.noContent().build();
    }
}