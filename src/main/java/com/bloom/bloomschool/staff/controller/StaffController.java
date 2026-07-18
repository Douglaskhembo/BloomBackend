package com.bloom.bloomschool.staff.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.staff.dto.StaffRequest;
import com.bloom.bloomschool.staff.service.StaffService;
import com.bloom.bloomschool.staff.util.Status;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.getAll(search)));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.getByUuid(uuid)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody StaffRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Staff created", staffService.create(req)));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable UUID uuid, @Valid @RequestBody StaffRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Staff updated", staffService.update(uuid, req)));
    }

    @PatchMapping("/{uuid}/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable UUID uuid, @RequestParam Status status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", staffService.updateStatus(uuid, status)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable UUID uuid) {
        staffService.delete(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Staff deleted"));
    }
}
