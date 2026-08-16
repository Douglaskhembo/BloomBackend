package com.bloom.bloomschool.staff.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
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
    private final PermissionResolver permissionResolver;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(@RequestParam(required = false) String search) {
        permissionResolver.requirePermission("STAFF_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(staffService.getAll(search)));
    }

    // ── Self-service (any authenticated staff, scoped server-side to their own record) ──
    // Read-only by design: profile fields are HR-controlled and edited only via the admin staff endpoints below.
    // Deliberately NOT permission-gated — every teacher portal page (My Classes, Attendance, Term
    // Reports, Performance) resolves the caller's own Staff record via getByUuid(profileRef), not
    // just getMyProfile(); gating either behind an admin-only STAFF_VIEW would break the whole
    // teacher portal for anyone without it.

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.ok(staffService.getMyProfile()));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.getByUuid(uuid)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody StaffRequest req) {
        permissionResolver.requirePermission("STAFF_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Staff created", staffService.create(req)));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable UUID uuid, @Valid @RequestBody StaffRequest req) {
        permissionResolver.requirePermission("STAFF_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Staff updated", staffService.update(uuid, req)));
    }

    @PatchMapping("/{uuid}/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable UUID uuid, @RequestParam Status status) {
        permissionResolver.requirePermission("STAFF_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Status updated", staffService.updateStatus(uuid, status)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("STAFF_MANAGE");
        staffService.delete(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Staff deleted"));
    }
}
