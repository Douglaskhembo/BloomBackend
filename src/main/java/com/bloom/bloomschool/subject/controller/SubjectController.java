package com.bloom.bloomschool.subject.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.subject.dto.SubjectRequestDto;
import com.bloom.bloomschool.subject.dto.SubjectResponseDto;
import com.bloom.bloomschool.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService service;

    @GetMapping("/get-all-subjects")
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> getAllSubjects() {
        return ResponseEntity.ok(ApiResponse.ok(service.get_all_subjects()));
    }

    @GetMapping("/get-subject/{uuid}")
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> getSubject(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.get_subject(uuid)));
    }

    @PostMapping("/create-subject")
    public ResponseEntity<ApiResponse<?>> createSubject(@RequestBody SubjectRequestDto req) {
        service.createSubject(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Subject created"));
    }

    @PutMapping("/update-subject/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateSubject(@PathVariable UUID uuid, @RequestBody SubjectRequestDto req) {
        service.update(uuid, req);
        return ResponseEntity.ok(ApiResponse.ok("Subject updated"));
    }

    @PatchMapping("/toggle-subject/{uuid}")
    public ResponseEntity<ApiResponse<?>> toggleSubject(@PathVariable UUID uuid) {
        service.toggleStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Subject status updated"));
    }

    @DeleteMapping("/delete-subject/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteSubject(@PathVariable UUID uuid) {
        service.deleteSubject(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Subject deleted"));
    }
}