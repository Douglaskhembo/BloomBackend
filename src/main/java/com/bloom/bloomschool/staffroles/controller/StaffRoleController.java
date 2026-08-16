package com.bloom.bloomschool.staffroles.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.staffroles.dto.StaffRoleRequest;
import com.bloom.bloomschool.staffroles.service.StaffRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff-roles")
@RequiredArgsConstructor
public class StaffRoleController {

    private final StaffRoleService staffRoleService;
    private final PermissionResolver permissionResolver;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(staffRoleService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody StaffRoleRequest req) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Staff role created", staffRoleService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @Valid @RequestBody StaffRoleRequest req) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Staff role updated", staffRoleService.update(id, req)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggle(@PathVariable Long id) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Toggled", staffRoleService.toggle(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        staffRoleService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Staff role deleted"));
    }
}
