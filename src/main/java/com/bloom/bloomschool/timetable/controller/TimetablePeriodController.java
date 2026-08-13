package com.bloom.bloomschool.timetable.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.timetable.dto.request.TimetablePeriodRequest;
import com.bloom.bloomschool.timetable.service.TimetablePeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/timetable/periods")
@RequiredArgsConstructor
public class TimetablePeriodController {

    private final TimetablePeriodService periodService;
    private final PermissionResolver permissionResolver;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(periodService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody TimetablePeriodRequest req) {
        permissionResolver.requirePermission("TIMETABLE_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Time slot created", periodService.create(req)));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable UUID uuid, @Valid @RequestBody TimetablePeriodRequest req) {
        permissionResolver.requirePermission("TIMETABLE_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Time slot updated", periodService.update(uuid, req)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("TIMETABLE_MANAGE");
        periodService.delete(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Time slot deleted"));
    }
}
