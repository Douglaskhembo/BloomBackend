package com.bloom.bloomschool.auth.controller;

import com.bloom.bloomschool.auth.dto.PermissionsBean;
import com.bloom.bloomschool.auth.dto.Requests.CreateUserRequest;
import com.bloom.bloomschool.auth.dto.Requests.OnboardStaffRequest;
import com.bloom.bloomschool.auth.dto.UserRolesDTO;
import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.auth.service.UserService;
import com.bloom.bloomschool.auth.utils.ApiResponse;
import com.bloom.bloomschool.auth.utils.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GenericResponse genericResponse;
    private final PermissionResolver permissionResolver;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAll() {
        permissionResolver.requirePermission("USER_VIEW");
        return genericResponse.response(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> getByUuid(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("USER_VIEW");
        return genericResponse.response(userService.getUserByUuid(uuid), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUserRequest request) {
        permissionResolver.requirePermission("USER_CREATE");
        userService.createUser(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/onboard-staff")
    public ResponseEntity<?> onboardStaff(@Valid @RequestBody OnboardStaffRequest request) {
        permissionResolver.requirePermission("USER_CREATE");
        userService.onboardStaff(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid,
                                    @RequestBody CreateUserRequest request) {
        permissionResolver.requirePermission("USER_EDIT");
        userService.updateUser(uuid, request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{uuid}/toggle-status")
    public ResponseEntity<ApiResponse<Object>> toggleStatus(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("USER_EDIT");
        userService.toggleUserStatus(uuid);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("USER_DELETE");
        userService.deleteUser(uuid);
        return genericResponse.response(null, HttpStatus.OK);
    }

    // ── Role assignment ──────────────────────────────────────────────────────

    @GetMapping("/{uuid}/assigned-roles")
    public ResponseEntity<ApiResponse<Object>> getAssignedRoles(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("USER_VIEW");
        return genericResponse.response(userService.getAssignedRoles(uuid), HttpStatus.OK);
    }

    @PostMapping("/assign-roles")
    public ResponseEntity<ApiResponse<Object>> assignRoles(@RequestBody UserRolesDTO dto) {
        permissionResolver.requirePermission("ROLE_ASSIGN");
        userService.assignRoles(dto);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @PostMapping("/unassign-roles")
    public ResponseEntity<ApiResponse<Object>> unassignRoles(@RequestBody UserRolesDTO dto) {
        permissionResolver.requirePermission("ROLE_ASSIGN");
        userService.unassignRoles(dto);
        return genericResponse.response(null, HttpStatus.OK);
    }

    // ── User-level permission overrides ──────────────────────────────────────

    @GetMapping("/{uuid}/effective-permissions")
    public ResponseEntity<ApiResponse<Object>> effectivePermissions(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("USER_VIEW");
        return genericResponse.response(userService.getUserEffectivePermissions(uuid), HttpStatus.OK);
    }

    @PostMapping("/grant-permission")
    public ResponseEntity<ApiResponse<Object>> grantPermission(@RequestBody PermissionsBean bean) {
        permissionResolver.requirePermission("PERMISSION_ASSIGN");
        userService.grantUserPermission(bean);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @PostMapping("/revoke-permission")
    public ResponseEntity<ApiResponse<Object>> revokePermission(@RequestBody PermissionsBean bean) {
        permissionResolver.requirePermission("PERMISSION_ASSIGN");
        userService.revokeUserPermission(bean);
        return genericResponse.response(null, HttpStatus.OK);
    }
}
