package com.bloom.bloomschool.users.controller;

import com.bloom.bloomschool.users.dto.PermissionsRequestDTO;
import com.bloom.bloomschool.users.dto.PermissionsResponseDTO;
import com.bloom.bloomschool.users.service.PermissionsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/permissions")
@RestController
public class PermissionsController {

    private final PermissionsService permService;

    // Get All Permissions
    @GetMapping
    public ResponseEntity<List<PermissionsResponseDTO>> findPermissions(){
        return ResponseEntity.ok(permService.getPermissions());
    }

    // Get Permissions by uuid
    @GetMapping("/{uuid}")
    public ResponseEntity<PermissionsResponseDTO> findPermissionsByUuid(@PathVariable UUID uuid){
        return ResponseEntity.ok(permService.getByUuid(uuid));
    }

    // Get Permissions by name
    @GetMapping("/name")
    public ResponseEntity<PermissionsResponseDTO> findPermissionsByName(@RequestParam String name){
        return ResponseEntity.ok(permService.getByName(name));
    }

    // Create Permissions
    @PostMapping
    public ResponseEntity<?> createPermissions(@RequestBody PermissionsRequestDTO req){
        permService.createPermission(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Update Permissions
    @PutMapping("/{uuid}")
    public ResponseEntity<?> updatePermissions(@PathVariable UUID uuid, @RequestBody PermissionsRequestDTO req){
        permService.updatePermissions(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // Delete Permissions
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deletePermissions(@PathVariable UUID uuid){
        permService.deletePermissions(uuid);
        return ResponseEntity.noContent().build();
    }
}