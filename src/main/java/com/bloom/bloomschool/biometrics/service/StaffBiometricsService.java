package com.bloom.bloomschool.biometrics.service;

import com.bloom.bloomschool.biometrics.dto.request.BioCaptureRequest;
import com.bloom.bloomschool.biometrics.dto.request.BioEnrollRequest;
import com.bloom.bloomschool.biometrics.dto.response.AttendanceResponse;
import com.bloom.bloomschool.biometrics.dto.response.BioDataResponse;
import com.bloom.bloomschool.biometrics.entity.StaffAttendance;
import com.bloom.bloomschool.biometrics.entity.StaffBioData;
import com.bloom.bloomschool.biometrics.repository.StaffAttendanceRepository;
import com.bloom.bloomschool.biometrics.repository.StaffBioDataRepository;
import com.bloom.bloomschool.biometrics.util.AttendanceStatus;
import com.bloom.bloomschool.biometrics.util.EnrollmentStatus;
import com.bloom.bloomschool.biometrics.util.EventType;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffBiometricsService {

    private final StaffRepository staffRepository;
    private final StaffBioDataRepository bioDataRepository;
    private final StaffAttendanceRepository attendanceRepository;

    @Transactional
    public BioDataResponse enroll(UUID staffUuid, BioEnrollRequest req) {
        Staff staff = staffRepository.findByUuid(staffUuid)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        if (bioDataRepository.existsByStaffId(staff.getId()))
            throw new IllegalArgumentException("Staff already has biometric data enrolled");

        StaffBioData bio = bioDataRepository.save(StaffBioData.builder()
                .staff(staff)
                .leftFingerprintTemplateRef(req.getLeftFingerprintTemplateRef())
                .leftFingerName(req.getLeftFingerName())
                .rightFingerprintTemplateRef(req.getRightFingerprintTemplateRef())
                .rightFingerName(req.getRightFingerName())
                .faceTemplateRef(req.getFaceTemplateRef())
                .enrolledDeviceId(req.getEnrolledDeviceId())
                .enrolledAt(LocalDateTime.now())
                .build());

        return toResponse(bio);
    }

    public BioDataResponse getBioData(UUID staffUuid) {
        Staff staff = staffRepository.findByUuid(staffUuid)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));
        StaffBioData bio = bioDataRepository.findByStaffId(staff.getId())
                .orElseThrow(() -> new EntityNotFoundException("No biometric data found for this staff"));
        return toResponse(bio);
    }

    @Transactional
    public BioDataResponse updateStatus(UUID bioUuid, EnrollmentStatus status) {
        StaffBioData bio = bioDataRepository.findByUuid(bioUuid)
                .orElseThrow(() -> new EntityNotFoundException("Bio data not found"));
        bio.setStatus(status);
        return toResponse(bioDataRepository.save(bio));
    }

    /** Called by the biometric device on scan — auto-detects clock-in vs clock-out */
    @Transactional
    public AttendanceResponse capture(BioCaptureRequest req) {
        StaffBioData bio = bioDataRepository.findByUuid(req.getBioDataUuid())
                .orElseThrow(() -> new EntityNotFoundException("Bio data not found"));

        if (bio.getStatus() != EnrollmentStatus.ACTIVE)
            throw new IllegalArgumentException("Biometric profile is not active");

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // If there's an open clock-in today, this scan is a clock-out
        return attendanceRepository.findOpenClockIn(bio.getStaff().getId(), today)
                .map(existing -> {
                    existing.setClockOut(now);
                    existing.setEventType(EventType.CLOCK_OUT);
                    existing.setRemarks(req.getRemarks());
                    return toAttendanceResponse(attendanceRepository.save(existing));
                })
                .orElseGet(() -> {
                    StaffAttendance att = attendanceRepository.save(StaffAttendance.builder()
                            .staff(bio.getStaff())
                            .bioData(bio)
                            .attendanceDate(today)
                            .clockIn(now)
                            .deviceId(req.getDeviceId())
                            .eventType(EventType.CLOCK_IN)
                            .status(resolveStaffStatus(now))
                            .remarks(req.getRemarks())
                            .build());
                    return toAttendanceResponse(att);
                });
    }

    public List<AttendanceResponse> getAttendance(UUID staffUuid, LocalDate from, LocalDate to) {
        Staff staff = staffRepository.findByUuid(staffUuid)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));
        return attendanceRepository
                .findByStaffIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(staff.getId(), from, to)
                .stream().map(this::toAttendanceResponse).toList();
    }

    public List<AttendanceResponse> getDailyAttendance(LocalDate date) {
        return attendanceRepository.findByAttendanceDateOrderByStaffId(date)
                .stream().map(this::toAttendanceResponse).toList();
    }

    /** Staff expected by 08:00 — after that is LATE */
    private AttendanceStatus resolveStaffStatus(LocalDateTime clockIn) {
        return clockIn.getHour() >= 8 && clockIn.getMinute() > 0
                ? AttendanceStatus.LATE
                : AttendanceStatus.PRESENT;
    }

    private BioDataResponse toResponse(StaffBioData b) {
        return BioDataResponse.builder()
                .uuid(b.getUuid())
                .ownerUuid(b.getStaff().getUuid().toString())
                .ownerName(b.getStaff().getFirstName() + " " + b.getStaff().getLastName())
                .ownerRef(b.getStaff().getStaffId())
                .leftFingerprintTemplateRef(b.getLeftFingerprintTemplateRef())
                .leftFingerName(b.getLeftFingerName().name())
                .rightFingerprintTemplateRef(b.getRightFingerprintTemplateRef())
                .rightFingerName(b.getRightFingerName().name())
                .faceTemplateRef(b.getFaceTemplateRef())
                .enrolledDeviceId(b.getEnrolledDeviceId())
                .enrolledAt(b.getEnrolledAt())
                .status(b.getStatus().name())
                .build();
    }

    private AttendanceResponse toAttendanceResponse(StaffAttendance a) {
        return AttendanceResponse.builder()
                .uuid(a.getUuid())
                .ownerUuid(a.getStaff().getUuid().toString())
                .ownerName(a.getStaff().getFirstName() + " " + a.getStaff().getLastName())
                .ownerRef(a.getStaff().getStaffId())
                .attendanceDate(a.getAttendanceDate())
                .clockInOrEntry(a.getClockIn())
                .clockOutOrExit(a.getClockOut())
                .deviceId(a.getDeviceId())
                .eventType(a.getEventType().name())
                .status(a.getStatus().name())
                .remarks(a.getRemarks())
                .build();
    }
}
