package com.bloom.bloomschool.biometrics.controller;

import com.bloom.bloomschool.attendance.util.OwnerType;
import com.bloom.bloomschool.auth.utils.ApiResponse;
import com.bloom.bloomschool.auth.utils.GenericResponse;
import com.bloom.bloomschool.biometrics.dto.request.BioCaptureRequest;
import com.bloom.bloomschool.biometrics.dto.request.BioIdentifyRequest;
import com.bloom.bloomschool.biometrics.dto.response.AttendanceResponse;
import com.bloom.bloomschool.biometrics.service.FingerprintIdentificationService;
import com.bloom.bloomschool.biometrics.service.IdentifiedOwner;
import com.bloom.bloomschool.biometrics.service.StaffBiometricsService;
import com.bloom.bloomschool.biometrics.service.StudentBiometricsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/biometrics")
@RequiredArgsConstructor
public class BiometricsCaptureController {

    private final FingerprintIdentificationService identificationService;
    private final StudentBiometricsService studentBiometricsService;
    private final StaffBiometricsService staffBiometricsService;
    private final GenericResponse genericResponse;

    @PostMapping("/capture")
    public ResponseEntity<ApiResponse<Object>> capture(@Valid @RequestBody BioIdentifyRequest req) {
        IdentifiedOwner identified = identificationService.identify(req.getImage(), req.getOwnerType());

        BioCaptureRequest captureReq = new BioCaptureRequest();
        captureReq.setBioDataUuid(identified.bioDataUuid());
        captureReq.setDeviceId(req.getDeviceId());
        captureReq.setRemarks(req.getRemarks());

        AttendanceResponse response = identified.ownerType() == OwnerType.STAFF
                ? staffBiometricsService.capture(captureReq)
                : studentBiometricsService.capture(captureReq);

        response.setOwnerType(identified.ownerType().name());
        response.setMatchScore(identified.score());

        return genericResponse.response(response, HttpStatus.OK);
    }
}
