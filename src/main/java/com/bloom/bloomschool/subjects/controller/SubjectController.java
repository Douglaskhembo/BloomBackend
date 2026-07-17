package com.bloom.bloomschool.subjects.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.subjects.dto.SubjectRequest;
import com.bloom.bloomschool.subjects.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(@RequestParam(required = false) String grade) {
        return ResponseEntity.ok(ApiResponse.ok(subjectService.getAll(grade)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody SubjectRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Subject created", subjectService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @Valid @RequestBody SubjectRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Subject updated", subjectService.update(id, req)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Toggled", subjectService.toggle(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Subject deleted"));
    }
}
