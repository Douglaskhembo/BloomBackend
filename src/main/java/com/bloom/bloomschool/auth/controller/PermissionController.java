package com.bloom.bloomschool.auth.controller;

import com.bloom.bloomschool.auth.dto.PermissionsBean;
import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.auth.service.PermissionService;
import com.bloom.bloomschool.auth.utils.ApiResponse;
import com.bloom.bloomschool.auth.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final GenericResponse genericResponse;
    private final PermissionResolver permissionResolver;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody PermissionsBean request) {
        permissionResolver.requirePermission("PERMISSION_ASSIGN");
        permissionService.createPermission(request);
        return genericResponse.response(null, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Object>> getAll() {
        permissionResolver.requirePermission("ROLE_VIEW");
        return genericResponse.response(permissionService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> getByUuid(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("ROLE_VIEW");
        return genericResponse.response(permissionService.getByUuid(uuid), HttpStatus.OK);
    }

    @GetMapping("/by-module")
    public ResponseEntity<ApiResponse<Object>> getByModule(@RequestParam UUID roleUuid,
                                                           @RequestParam UUID moduleUuid) {
        permissionResolver.requirePermission("ROLE_VIEW");
        return genericResponse.response(permissionService.getByModule(roleUuid, moduleUuid), HttpStatus.OK);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> update(@PathVariable UUID uuid, @RequestParam String name) {
        permissionResolver.requirePermission("PERMISSION_ASSIGN");
        permissionService.updatePermission(uuid, name);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("PERMISSION_ASSIGN");
        permissionService.deletePermission(uuid);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @PostMapping("/grant")
    public ResponseEntity<ApiResponse<Object>> grant(@RequestBody PermissionsBean bean) {
        permissionResolver.requirePermission("PERMISSION_ASSIGN");
        permissionService.grantPermissionToRole(bean);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @PostMapping("/revoke")
    public ResponseEntity<ApiResponse<Object>> revoke(@RequestBody PermissionsBean bean) {
        permissionResolver.requirePermission("PERMISSION_ASSIGN");
        permissionService.revokePermissionFromRole(bean);
        return genericResponse.response(null, HttpStatus.OK);
    }
}
