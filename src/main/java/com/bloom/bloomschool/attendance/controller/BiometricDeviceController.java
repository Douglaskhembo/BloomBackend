package com.bloom.bloomschool.attendance.controller;

import com.bloom.bloomschool.attendance.dto.request.DeviceRequest;
import com.bloom.bloomschool.attendance.entity.BiometricDevice;
import com.bloom.bloomschool.attendance.service.BiometricDeviceService;
import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/attendance/devices")
@RequiredArgsConstructor
public class BiometricDeviceController {

    private final BiometricDeviceService deviceService;
    private final PermissionResolver permissionResolver;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        permissionResolver.requirePermission("ATTENDANCE_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok(deviceService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody DeviceRequest req) {
        permissionResolver.requirePermission("ATTENDANCE_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Device registered — copy the API key now, it will not be shown again", deviceService.register(req)));
    }

    @PostMapping("/{uuid}/regenerate-key")
    public ResponseEntity<ApiResponse<?>> regenerateKey(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("ATTENDANCE_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("API key regenerated — copy it now, it will not be shown again", deviceService.regenerateKey(uuid)));
    }

    @PatchMapping("/{uuid}/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable UUID uuid, @RequestParam BiometricDevice.DeviceStatus status) {
        permissionResolver.requirePermission("ATTENDANCE_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Status updated", deviceService.updateStatus(uuid, status)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("ATTENDANCE_MANAGE");
        deviceService.delete(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Device removed"));
    }
}
