package com.bloom.bloomschool.staff.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
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

    @GetMapping("/get-all-staff-roles")
    public ResponseEntity<ApiResponse<List<StaffRoleResponseDto>>> getAllStaffRoles(){
        return ResponseEntity.ok(ApiResponse.ok(service.getAllRoles()));
    }

    @GetMapping("/get-staff-role/{uuid}")
    public ResponseEntity<ApiResponse<StaffRoleResponseDto>> getStaffRole(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.getStaffRole(uuid)));
    }

    @PostMapping("/create-staff-role")
    public ResponseEntity<ApiResponse<?>> createStaffRole(@RequestBody StaffRoleRequestDto req){
        service.createStaffRole(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Staff role created"));
    }

    @PutMapping("/update-staff-role/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateStaffRole(@PathVariable UUID uuid, @RequestBody StaffRoleRequestDto req){
        service.updateStaffRole(uuid, req);
        return ResponseEntity.ok(ApiResponse.ok("Staff role updated"));
    }

    @PatchMapping("/toggle-staff-role/{uuid}")
    public ResponseEntity<ApiResponse<?>> toggleStaffRole(@PathVariable UUID uuid) {
        service.toggleStaffRoleStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Staff role status updated"));
    }

    @DeleteMapping("/delete-staff-role/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteStaffRole(@PathVariable UUID uuid) {
        service.deleteStaffRole(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Staff role deleted"));
    }
}