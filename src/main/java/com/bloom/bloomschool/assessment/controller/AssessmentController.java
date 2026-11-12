package com.bloom.bloomschool.assessment.controller;


import com.bloom.bloomschool.assessment.dto.*;
import com.bloom.bloomschool.assessment.service.AssessmentService;
import com.bloom.bloomschool.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assessment")
@RequiredArgsConstructor
public class AssessmentController {
    private final AssessmentService service;

    @GetMapping("/get-all-assessments")
    public ResponseEntity<ApiResponse<List<AssessmentResponseDto>>> getAllAssessments(){
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/get-assessment/{uuid}")
    public ResponseEntity<ApiResponse<AssessmentResponseDto>> getAssessment(@PathVariable UUID uuid){
        return ResponseEntity.ok(ApiResponse.ok(service.getAssessment(uuid)));
    }

    @GetMapping("/get-all-assessment-marks")
    public ResponseEntity<ApiResponse<List<AssessmentMarkResponseDto>>> getAllAssessmentMarks(){
        return ResponseEntity.ok(ApiResponse.ok(service.getMarks()));
    }

    @PostMapping("/create-assessment")
    public ResponseEntity<ApiResponse<?>> createAssessment(@RequestBody AssessmentRequestDto req){
        service.createAssessment(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Assessment created"));
    }

    @PostMapping("/create-assessment-marks/{assessmentUuid}")
    public ResponseEntity<ApiResponse<?>> createAssessmentMarks(
            @PathVariable UUID assessmentUuid,
            @RequestBody MarkEntryRequestDto req){
        service.saveMarks(assessmentUuid, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Assessment marks created"));
    }

    @PutMapping("/update-assessment/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateAssessment(@PathVariable UUID uuid, @RequestBody AssessmentRequestDto req){
        service.updateAssessment(uuid, req);
        return ResponseEntity.ok(ApiResponse.ok("Assessment updated"));
    }

    @DeleteMapping("/delete-assessment/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteAssessment(@PathVariable UUID uuid){
        service.deleteAssessment(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Assessment deleted"));
    }
}
