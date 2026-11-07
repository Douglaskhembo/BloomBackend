package com.bloom.bloomschool.staff.controller;

import com.bloom.bloomschool.staff.dto.StaffRoleRequestDto;
import com.bloom.bloomschool.staff.dto.StaffRoleResponseDto;
import com.bloom.bloomschool.staff.service.StaffRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff-roles")
@RequiredArgsConstructor
public class StaffRoleController {

    private final StaffRoleService service;

    @GetMapping("/allroles")
    public ResponseEntity<List<StaffRoleResponseDto>> getAll(){
        return ResponseEntity.ok(service.getAllRoles());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<StaffRoleResponseDto> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getStaffRole(uuid));
    }

    @PostMapping("/createStaffRole")
    public ResponseEntity<?> create(@RequestBody StaffRoleRequestDto req){
        service.createStaffRole(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/updateStaffRole/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody StaffRoleRequestDto req){
        service.updateStaffRole(uuid, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/toggle-status/{uuid}")
    public ResponseEntity<?> toggleStatus(@PathVariable UUID uuid) {
        service.toggleStaffRoleStatus(uuid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteStaffRole/{uuid}")
    public ResponseEntity<Void> deleteStaffRole(@PathVariable UUID uuid) {
        service.deleteStaffRole(uuid);
        return ResponseEntity.noContent().build();
    }
}