package com.bloom.bloomschool.users.controller;

import com.bloom.bloomschool.users.dto.RolesResponseDTO;
import com.bloom.bloomschool.users.dto.RolesRequestDTO;
import com.bloom.bloomschool.users.service.RolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/roles")
public class RoleController {
    private final RolesService roleService;

    // Get All Roles
    @GetMapping
    public ResponseEntity<List<RolesResponseDTO>> getRoles() {
        return ResponseEntity.ok(roleService.getRoles());
    }

    // Get Role By Name
    @GetMapping("/search/name/{name}")
    public ResponseEntity<RolesResponseDTO> getRoleByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.getRoleByName(name));
    }

    // Get Role By UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<RolesResponseDTO> getRoleByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(roleService.getRoleByUuid(uuid));
    }

    // Create Role
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RolesRequestDTO req) {
        roleService.createRole(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Update Role
    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateRole(@PathVariable UUID uuid, @RequestBody RolesRequestDTO req) {
        roleService.updateRole(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // Assign User to Role (Role-Centric)
    @PostMapping("/{roleUuid}/users/{userUuid}")
    public ResponseEntity<?> addUserToRole(@PathVariable UUID roleUuid, @PathVariable UUID userUuid) {
        roleService.addUserToRole(roleUuid, userUuid);
        return ResponseEntity.ok().build();
    }

    // Remove User from Role
    @DeleteMapping("/{roleUuid}/users/{userUuid}")
    public ResponseEntity<?> removeUserFromRole(@PathVariable UUID roleUuid, @PathVariable UUID userUuid) {
        roleService.removeUserFromRole(roleUuid, userUuid);
        return ResponseEntity.noContent().build();
    }

    // Delete Role
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteRole(@PathVariable UUID uuid) {
        roleService.deleteRole(uuid);
        return ResponseEntity.noContent().build();
    }
}