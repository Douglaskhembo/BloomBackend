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
