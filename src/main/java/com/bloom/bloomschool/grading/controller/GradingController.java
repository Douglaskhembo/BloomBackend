package com.bloom.bloomschool.grading.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.grading.dto.GradingEntriesRequest;
import com.bloom.bloomschool.grading.dto.GradingStructureRequest;
import com.bloom.bloomschool.grading.service.GradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/grading-structures")
@RequiredArgsConstructor
public class GradingController {

    private final GradingService gradingService;
    private final PermissionResolver permissionResolver;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(gradingService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody GradingStructureRequest req) {
        permissionResolver.requirePermission("SUBJECTS_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Grading structure created", gradingService.create(req)));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> replaceEntries(@PathVariable UUID uuid, @Valid @RequestBody GradingEntriesRequest req) {
        permissionResolver.requirePermission("SUBJECTS_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Grading structure updated", gradingService.replaceEntries(uuid, req)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("SUBJECTS_MANAGE");
        gradingService.delete(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Grading structure deleted"));
    }
}
