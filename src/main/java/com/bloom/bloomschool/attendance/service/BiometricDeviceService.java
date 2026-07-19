package com.bloom.bloomschool.attendance.service;

import com.bloom.bloomschool.attendance.dto.request.DeviceRequest;
import com.bloom.bloomschool.attendance.dto.response.DeviceResponse;
import com.bloom.bloomschool.attendance.entity.BiometricDevice;
import com.bloom.bloomschool.attendance.repository.BiometricDeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BiometricDeviceService {

    private final BiometricDeviceRepository deviceRepo;
    private final PasswordEncoder passwordEncoder;

    public List<DeviceResponse> getAll() {
        return deviceRepo.findAll().stream().map(d -> toResponse(d, null)).toList();
    }

    @Transactional
    public DeviceResponse register(DeviceRequest req) {
        if (deviceRepo.existsByDeviceCode(req.getDeviceCode()))
            throw new IllegalArgumentException("Device code '" + req.getDeviceCode() + "' is already registered");

        String rawApiKey = UUID.randomUUID().toString().replace("-", "");
        BiometricDevice device = deviceRepo.save(BiometricDevice.builder()
                .deviceCode(req.getDeviceCode())
                .name(req.getName())
                .location(req.getLocation())
                .deviceType(req.getDeviceType())
                .apiKeyHash(passwordEncoder.encode(rawApiKey))
                .status(BiometricDevice.DeviceStatus.ACTIVE)
                .build());

        return toResponse(device, rawApiKey);
    }

    @Transactional
    public DeviceResponse regenerateKey(UUID uuid) {
        BiometricDevice device = getByUuid(uuid);
        String rawApiKey = UUID.randomUUID().toString().replace("-", "");
        device.setApiKeyHash(passwordEncoder.encode(rawApiKey));
        deviceRepo.save(device);
        return toResponse(device, rawApiKey);
    }

    @Transactional
    public DeviceResponse updateStatus(UUID uuid, BiometricDevice.DeviceStatus status) {
        BiometricDevice device = getByUuid(uuid);
        device.setStatus(status);
        return toResponse(deviceRepo.save(device), null);
    }

    @Transactional
    public void delete(UUID uuid) {
        deviceRepo.delete(getByUuid(uuid));
    }

    /** Validates a device's presented raw API key and returns the matching ACTIVE device, or empty. */
    @Transactional
    public BiometricDevice authenticate(String deviceCode, String rawApiKey) {
        BiometricDevice device = deviceRepo.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new EntityNotFoundException("Unknown device code '" + deviceCode + "'"));
        if (device.getStatus() != BiometricDevice.DeviceStatus.ACTIVE)
            throw new IllegalArgumentException("Device is not active");
        if (rawApiKey == null || !passwordEncoder.matches(rawApiKey, device.getApiKeyHash()))
            throw new SecurityException("Invalid device API key");

        device.setLastSeenAt(LocalDateTime.now());
        deviceRepo.save(device);
        return device;
    }

    private BiometricDevice getByUuid(UUID uuid) {
        return deviceRepo.findByUuid(uuid).orElseThrow(() -> new EntityNotFoundException("Device not found"));
    }

    private DeviceResponse toResponse(BiometricDevice d, String rawApiKey) {
        return DeviceResponse.builder()
                .uuid(d.getUuid())
                .deviceCode(d.getDeviceCode())
                .name(d.getName())
                .location(d.getLocation())
                .deviceType(d.getDeviceType().name())
                .status(d.getStatus().name())
                .lastSeenAt(d.getLastSeenAt())
                .apiKey(rawApiKey)
                .build();
    }
}
