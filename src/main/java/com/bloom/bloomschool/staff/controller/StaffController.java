package com.bloom.bloomschool.staff.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.staff.dto.StaffRequestDto;
import com.bloom.bloomschool.staff.dto.StaffResponseDto;
import com.bloom.bloomschool.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService service;

    @GetMapping("/get-all-staff")
    public ResponseEntity<ApiResponse<List<StaffResponseDto>>> getAllStaff(){
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/get-staff/{uuid}")
    public ResponseEntity<ApiResponse<StaffResponseDto>> getStaff(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByUuid(uuid)));
    }

    @PostMapping("/create-staff")
    public ResponseEntity<ApiResponse<?>> createStaff(@RequestBody StaffRequestDto req){
        service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Staff created"));
    }

    @PutMapping("/update-staff/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateStaff(@PathVariable UUID uuid, @RequestBody StaffRequestDto req){
        service.update(uuid, req);
        return ResponseEntity.ok(ApiResponse.ok("Staff updated"));
    }

    @PatchMapping("/toggle-staff/{uuid}")
    public ResponseEntity<ApiResponse<?>> toggleStaff(@PathVariable UUID uuid) {
        service.toggleStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Staff status updated"));
    }

    @DeleteMapping("/delete-staff/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteStaff(@PathVariable UUID uuid) {
        service.deleteStaff(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Staff deleted"));
    }
}