package com.bloom.bloomschool.attendance.controller;

import com.bloom.bloomschool.attendance.dto.request.DeviceCaptureRequest;
import com.bloom.bloomschool.attendance.service.DeviceCaptureService;
import com.bloom.bloomschool.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public — physical devices (or bridge/agent software) call this on every scan, no JWT.
 * Authenticated instead by the X-Device-Code / X-Device-Key headers against the device
 * registry (see {@link com.bloom.bloomschool.attendance.service.BiometricDeviceService}).
 */
@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class DeviceCaptureController {

    private static final Logger log = LoggerFactory.getLogger(DeviceCaptureController.class);

    private final DeviceCaptureService captureService;

    @PostMapping("/device-capture")
    public ResponseEntity<ApiResponse<?>> capture(
            @RequestHeader("X-Device-Code") String deviceCode,
            @RequestHeader("X-Device-Key") String apiKey,
            @Valid @RequestBody DeviceCaptureRequest req) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(captureService.capture(deviceCode, apiKey, req)));
        } catch (SecurityException e) {
            log.warn("Rejected device capture from '{}': {}", deviceCode, e.getMessage());
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }
}
