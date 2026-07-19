package com.bloom.bloomschool.attendance.controller;

import com.bloom.bloomschool.attendance.dto.request.DeviceRequest;
import com.bloom.bloomschool.attendance.entity.BiometricDevice;
import com.bloom.bloomschool.attendance.service.BiometricDeviceService;
import com.bloom.bloomschool.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/** Admin-managed registry of physical biometric devices installed around the school. */
@RestController
@RequestMapping("/attendance/devices")
@RequiredArgsConstructor
public class BiometricDeviceController {

    private final BiometricDeviceService deviceService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(deviceService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody DeviceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Device registered — copy the API key now, it will not be shown again", deviceService.register(req)));
    }

    @PostMapping("/{uuid}/regenerate-key")
    public ResponseEntity<ApiResponse<?>> regenerateKey(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok("API key regenerated — copy it now, it will not be shown again", deviceService.regenerateKey(uuid)));
    }

    @PatchMapping("/{uuid}/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable UUID uuid, @RequestParam BiometricDevice.DeviceStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", deviceService.updateStatus(uuid, status)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable UUID uuid) {
        deviceService.delete(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Device removed"));
    }
}
