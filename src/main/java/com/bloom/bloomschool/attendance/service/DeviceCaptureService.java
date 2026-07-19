package com.bloom.bloomschool.attendance.service;

import com.bloom.bloomschool.attendance.dto.request.DeviceCaptureRequest;
import com.bloom.bloomschool.attendance.entity.BiometricDevice;
import com.bloom.bloomschool.biometrics.dto.request.BioCaptureRequest;
import com.bloom.bloomschool.biometrics.dto.response.AttendanceResponse;
import com.bloom.bloomschool.biometrics.entity.StaffBioData;
import com.bloom.bloomschool.biometrics.entity.StudentBioData;
import com.bloom.bloomschool.biometrics.repository.StaffBioDataRepository;
import com.bloom.bloomschool.biometrics.repository.StudentBioDataRepository;
import com.bloom.bloomschool.biometrics.service.StaffBiometricsService;
import com.bloom.bloomschool.biometrics.service.StudentBiometricsService;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Entry point for real hardware (or a bridge/agent it's paired with): authenticates the
 * device by its API key, resolves the scanned person by their human-readable ref, then
 * delegates to the same {@code StaffBiometricsService}/{@code StudentBiometricsService}
 * capture logic the web "test scan" path uses — one clock-in/out engine, two ways in.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeviceCaptureService {

    private final BiometricDeviceService deviceService;
    private final StaffRepository staffRepository;
    private final StudentRepository studentRepository;
    private final StaffBioDataRepository staffBioDataRepository;
    private final StudentBioDataRepository studentBioDataRepository;
    private final StaffBiometricsService staffBiometricsService;
    private final StudentBiometricsService studentBiometricsService;

    public AttendanceResponse capture(String deviceCode, String apiKey, DeviceCaptureRequest req) {
        BiometricDevice device = deviceService.authenticate(deviceCode, apiKey);

        BioCaptureRequest captureReq = new BioCaptureRequest();
        captureReq.setDeviceId(device.getDeviceCode());
        captureReq.setRemarks(req.getRemarks());

        return switch (req.getOwnerType()) {
            case STAFF -> {
                Staff staff = staffRepository.findByStaffId(req.getOwnerRef())
                        .orElseThrow(() -> new EntityNotFoundException("No staff with staffId '" + req.getOwnerRef() + "'"));
                StaffBioData bio = staffBioDataRepository.findByStaffId(staff.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Staff has no enrolled biometrics"));
                captureReq.setBioDataUuid(bio.getUuid());
                yield staffBiometricsService.capture(captureReq);
            }
            case STUDENT -> {
                Student student = studentRepository.findByAdmissionNumber(req.getOwnerRef())
                        .orElseThrow(() -> new EntityNotFoundException("No student with admission number '" + req.getOwnerRef() + "'"));
                StudentBioData bio = studentBioDataRepository.findByStudentId(student.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Student has no enrolled biometrics"));
                captureReq.setBioDataUuid(bio.getUuid());
                yield studentBiometricsService.capture(captureReq);
            }
        };
    }
}
