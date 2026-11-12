package com.bloom.bloomschool.gradeLevel.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
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

    @GetMapping("/get-all-grades")
    public ResponseEntity<ApiResponse<List<GradeResponseDto>>> getAllGrades(){
        return ResponseEntity.ok(ApiResponse.ok(service.get_all_grades()));
    }

    @GetMapping("/get-grade/{uuid}")
    public ResponseEntity<ApiResponse<GradeResponseDto>> getGrade(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.get_grade(uuid)));
    }

    @PostMapping("/create-grade")
    public ResponseEntity<ApiResponse<?>> createGrade(@RequestBody GradeRequestDto req){
        service.createGrade(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Grade created"));
    }

    @PutMapping("/update-grade/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateGrade(@PathVariable UUID uuid, @RequestBody GradeRequestDto req){
        service.update(uuid, req);
        return ResponseEntity.ok(ApiResponse.ok("Grade updated"));
    }

    @PatchMapping("/toggle-grade/{uuid}")
    public ResponseEntity<ApiResponse<?>> toggleGrade(@PathVariable UUID uuid){
        service.toggleStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Grade status updated"));
    }

    @DeleteMapping("/delete-grade/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteGrade(@PathVariable UUID uuid){
        service.deleteGrade(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Grade deleted"));
    }
}