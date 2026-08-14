package com.bloom.bloomschool.attendance.service;

import com.bloom.bloomschool.attendance.dto.request.DeviceCaptureRequest;
import com.bloom.bloomschool.attendance.entity.BiometricDevice;
import com.bloom.bloomschool.attendance.util.OwnerType;
import com.bloom.bloomschool.biometrics.dto.request.BioCaptureRequest;
import com.bloom.bloomschool.biometrics.dto.response.AttendanceResponse;
import com.bloom.bloomschool.biometrics.service.FingerprintIdentificationService;
import com.bloom.bloomschool.biometrics.service.IdentifiedOwner;
import com.bloom.bloomschool.biometrics.service.StaffBiometricsService;
import com.bloom.bloomschool.biometrics.service.StudentBiometricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Entry point for real hardware (or a bridge/agent it's paired with): authenticates the
 * device by its API key, identifies the scanned fingerprint via 1:N matching against every
 * enrolled student/staff (the same {@link FingerprintIdentificationService} the web "identify"
 * test path uses), then delegates to the same {@code StaffBiometricsService}/
 * {@code StudentBiometricsService} capture logic — one clock-in/out engine, two ways in.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeviceCaptureService {

    private final BiometricDeviceService deviceService;
    private final FingerprintIdentificationService identificationService;
    private final StaffBiometricsService staffBiometricsService;
    private final StudentBiometricsService studentBiometricsService;

    public AttendanceResponse capture(String deviceCode, String apiKey, DeviceCaptureRequest req) {
        BiometricDevice device = deviceService.authenticate(deviceCode, apiKey);

        IdentifiedOwner identified = identificationService.identify(req.getImage(), req.getOwnerType());

        BioCaptureRequest captureReq = new BioCaptureRequest();
        captureReq.setBioDataUuid(identified.bioDataUuid());
        captureReq.setDeviceId(device.getDeviceCode());
        captureReq.setRemarks(req.getRemarks());

        AttendanceResponse response = identified.ownerType() == OwnerType.STAFF
                ? staffBiometricsService.capture(captureReq)
                : studentBiometricsService.capture(captureReq);

        response.setOwnerType(identified.ownerType().name());
        response.setMatchScore(identified.score());
        return response;
    }
}
