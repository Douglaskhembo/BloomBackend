package com.bloom.bloomschool.biometrics.service;

import com.bloom.bloomschool.biometrics.dto.request.BioCaptureRequest;
import com.bloom.bloomschool.biometrics.dto.request.BioEnrollRequest;
import com.bloom.bloomschool.biometrics.dto.response.AttendanceResponse;
import com.bloom.bloomschool.biometrics.dto.response.BioDataResponse;
import com.bloom.bloomschool.biometrics.entity.StudentAttendance;
import com.bloom.bloomschool.biometrics.entity.StudentBioData;
import com.bloom.bloomschool.biometrics.repository.StudentAttendanceRepository;
import com.bloom.bloomschool.biometrics.repository.StudentBioDataRepository;
import com.bloom.bloomschool.biometrics.util.AttendanceStatus;
import com.bloom.bloomschool.biometrics.util.EnrollmentStatus;
import com.bloom.bloomschool.biometrics.util.EventType;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
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
public class StudentBiometricsService {

    private final StudentRepository studentRepository;
    private final StudentBioDataRepository bioDataRepository;
    private final StudentAttendanceRepository attendanceRepository;

    @Transactional
    public BioDataResponse enroll(UUID studentUuid, BioEnrollRequest req) {
        Student student = studentRepository.findByUuid(studentUuid)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        if (bioDataRepository.existsByStudentId(student.getId()))
            throw new IllegalArgumentException("Student already has biometric data enrolled");

        StudentBioData bio = bioDataRepository.save(StudentBioData.builder()
                .student(student)
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

    public BioDataResponse getBioData(UUID studentUuid) {
        Student student = studentRepository.findByUuid(studentUuid)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        StudentBioData bio = bioDataRepository.findByStudentId(student.getId())
                .orElseThrow(() -> new EntityNotFoundException("No biometric data found for this student"));
        return toResponse(bio);
    }

    @Transactional
    public BioDataResponse updateStatus(UUID bioUuid, EnrollmentStatus status) {
        StudentBioData bio = bioDataRepository.findByUuid(bioUuid)
                .orElseThrow(() -> new EntityNotFoundException("Bio data not found"));
        bio.setStatus(status);
        return toResponse(bioDataRepository.save(bio));
    }

    /** Called by the biometric device on scan — auto-detects entry vs exit */
    @Transactional
    public AttendanceResponse capture(BioCaptureRequest req) {
        StudentBioData bio = bioDataRepository.findByUuid(req.getBioDataUuid())
                .orElseThrow(() -> new EntityNotFoundException("Bio data not found"));

        if (bio.getStatus() != EnrollmentStatus.ACTIVE)
            throw new IllegalArgumentException("Biometric profile is not active");

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // If there's an open entry today, this scan is an exit
        return attendanceRepository.findOpenEntry(bio.getStudent().getId(), today)
                .map(existing -> {
                    existing.setExitTime(now);
                    existing.setEventType(EventType.EXIT);
                    existing.setRemarks(req.getRemarks());
                    return toAttendanceResponse(attendanceRepository.save(existing));
                })
                .orElseGet(() -> {
                    StudentAttendance att = attendanceRepository.save(StudentAttendance.builder()
                            .student(bio.getStudent())
                            .bioData(bio)
                            .attendanceDate(today)
                            .entryTime(now)
                            .deviceId(req.getDeviceId())
                            .eventType(EventType.ENTRY)
                            .status(resolveStudentStatus(now))
                            .remarks(req.getRemarks())
                            .build());
                    return toAttendanceResponse(att);
                });
    }

    public List<AttendanceResponse> getAttendance(UUID studentUuid, LocalDate from, LocalDate to) {
        Student student = studentRepository.findByUuid(studentUuid)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        return attendanceRepository
                .findByStudentIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(student.getId(), from, to)
                .stream().map(this::toAttendanceResponse).toList();
    }

    public List<AttendanceResponse> getDailyAttendance(LocalDate date) {
        return attendanceRepository.findByAttendanceDateOrderByStudentId(date)
                .stream().map(this::toAttendanceResponse).toList();
    }

    /** Students expected by 07:30 — after that is LATE */
    private AttendanceStatus resolveStudentStatus(LocalDateTime entryTime) {
        if (entryTime.getHour() > 7 || (entryTime.getHour() == 7 && entryTime.getMinute() > 30))
            return AttendanceStatus.LATE;
        return AttendanceStatus.PRESENT;
    }

    private BioDataResponse toResponse(StudentBioData b) {
        return BioDataResponse.builder()
                .uuid(b.getUuid())
                .ownerUuid(b.getStudent().getUuid().toString())
                .ownerName(b.getStudent().getFirstName() + " " + b.getStudent().getLastName())
                .ownerRef(b.getStudent().getAdmissionNumber())
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

    private AttendanceResponse toAttendanceResponse(StudentAttendance a) {
        return AttendanceResponse.builder()
                .uuid(a.getUuid())
                .ownerUuid(a.getStudent().getUuid().toString())
                .ownerName(a.getStudent().getFirstName() + " " + a.getStudent().getLastName())
                .ownerRef(a.getStudent().getAdmissionNumber())
                .attendanceDate(a.getAttendanceDate())
                .clockInOrEntry(a.getEntryTime())
                .clockOutOrExit(a.getExitTime())
                .deviceId(a.getDeviceId())
                .eventType(a.getEventType().name())
                .status(a.getStatus().name())
                .remarks(a.getRemarks())
                .build();
    }
}
