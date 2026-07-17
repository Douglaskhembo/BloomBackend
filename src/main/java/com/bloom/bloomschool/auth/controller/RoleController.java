package com.bloom.bloomschool.auth.controller;

import com.bloom.bloomschool.auth.dto.Requests.CreateRoleRequest;
import com.bloom.bloomschool.auth.service.RoleService;
import com.bloom.bloomschool.auth.utils.ApiResponse;
import com.bloom.bloomschool.auth.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final GenericResponse genericResponse;

    @PostMapping("/createRole")
    public ResponseEntity<ApiResponse<Object>> createRole(@RequestBody CreateRoleRequest request) {
        roleService.createRole(request);
        return genericResponse.response(null, HttpStatus.CREATED);
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<ApiResponse<Object>> getRoleByUuid(@PathVariable UUID uuid) {
        return genericResponse.response(roleService.getRoleByUuid(uuid), HttpStatus.OK);
    }

    @GetMapping("/name/{roleName}")
    public ResponseEntity<ApiResponse<Object>> getRoleByName(@PathVariable String roleName) {
        return genericResponse.response(roleService.getRoleByName(roleName), HttpStatus.OK);
    }

    @GetMapping("/allRoles")
    public ResponseEntity<ApiResponse<Object>> getAllRoles() {
        return genericResponse.response(roleService.getAllRoles(), HttpStatus.OK);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> updateRole(@PathVariable UUID uuid,
                                                          @RequestBody CreateRoleRequest request) {
        roleService.updateRole(uuid, request);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> deleteRole(@PathVariable UUID uuid) {
        roleService.deleteRole(uuid);
        return genericResponse.response(null, HttpStatus.OK);
    }
}
