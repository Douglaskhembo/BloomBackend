package com.bloom.bloomschool.gradeLevel.controller;

import com.bloom.bloomschool.gradeLevel.dto.GradeDto;
import com.bloom.bloomschool.gradeLevel.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
    public ResponseEntity<List<GradeDto>> get_all_grades(){return ResponseEntity.ok(service.get_all_grades());}

    @GetMapping("/grade/{uuid}")
    public ResponseEntity<GradeDto> get_grade(@PathVariable UUID uuid)throws BadRequestException {
        return ResponseEntity.ok(service.get_grade(uuid));
    }

    @PostMapping("/createGrade")
    public ResponseEntity<?> create(@RequestBody GradeDto req){
        service.createGrade(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("updateGrade/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody GradeDto req){
        service.update(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("toggleStatus/{uuid}")
    public ResponseEntity<?> toggleStatus(@PathVariable UUID uuid){
        service.toggleStatus(uuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("deleteGrade/{uuid}")
    public ResponseEntity<?> deleteGrade(@PathVariable UUID uuid){
        service.deleteGrade(uuid);
        return ResponseEntity.noContent().build();
    }
}
