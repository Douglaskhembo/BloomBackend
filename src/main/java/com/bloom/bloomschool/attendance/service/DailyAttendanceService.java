package com.bloom.bloomschool.attendance.service;

import com.bloom.bloomschool.attendance.dto.DailyAttendanceRequestDTO;
import com.bloom.bloomschool.attendance.dto.DailyAttendanceResponseDTO;
import com.bloom.bloomschool.attendance.entity.DailyAttendance;
import com.bloom.bloomschool.attendance.entity.AttendanceStatus;
import com.bloom.bloomschool.attendance.repository.DailyAttendanceRepository;
import com.bloom.bloomschool.student.entity.Student;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class DailyAttendanceService {


    private final DailyAttendanceRepository attendanceRepo;
    private final StudentRepository studentRepo;

    private DailyAttendance findEntity(UUID uuid){
        return attendanceRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("The requested Daily attendance does not exist"));
    }

    public List<DailyAttendanceResponseDTO> getAttendancesByStudentUuid(UUID studentUuid) {
        if (!studentRepo.existsByUuid(studentUuid)) {
            throw new ResourceNotFoundException("Student not found with given UUID");
        }

        return attendanceRepo.findByStudentUuid(studentUuid).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private DailyAttendanceResponseDTO mapToResponseDTO(DailyAttendance a) {
        return DailyAttendanceResponseDTO.builder()
                .uuid(a.getUuid())
                .markedAt(a.getMarkedAt())
                .markedBy(a.getMarkedBy())
                .attendanceDate(a.getAttendanceDate())
                .stream(a.getStream())
                .grade(a.getGrade())
                .status(a.getStatus())
                .studentUuid(a.getStudent() != null ? a.getStudent().getUuid() : null)
                .studentAdmissionNumber(a.getStudent() != null ? a.getStudent().getAdmissionNumber() : null)
                .build();
    }

    public List<DailyAttendanceResponseDTO> getAllAttendances() {
        return attendanceRepo.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public DailyAttendanceResponseDTO getAttendanceByUuid(UUID uuid) {
        DailyAttendance a = findEntity(uuid);
        return mapToResponseDTO(a);
    }

    public List<DailyAttendanceResponseDTO> getAttendancesByGradeAndStream(String grade, String stream) {
        return attendanceRepo.findByGradeAndStream(grade, stream).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public List<DailyAttendanceResponseDTO> getAttendancesByStudentAdmission(String admissionNumber) {
        return attendanceRepo.findByStudentAdmissionNumber(admissionNumber).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public void createAttendance(DailyAttendanceRequestDTO req) {
        Student student = studentRepo.findByUuid(req.getStudentUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with given UUID"));

        if (attendanceRepo.existsByStudentUuidAndAttendanceDate(req.getStudentUuid(), req.getAttendanceDate())) {
            throw new RuntimeException("Attendance has already been marked for this student on this date.");
        }

        attendanceRepo.save(DailyAttendance.builder()
                .markedAt(req.getMarkedAt())
                .markedBy(req.getMarkedBy())
                .attendanceDate(req.getAttendanceDate())
                .stream(req.getStream())
                .grade(req.getGrade())
                .student(student)
                .status(req.getStatus() != null ? req.getStatus() : AttendanceStatus.PRESENT)
                .build());
    }

    @Transactional
    public void updateAttendance(UUID uuid, DailyAttendanceRequestDTO req) {
        DailyAttendance a = findEntity(uuid);

        Student student = studentRepo.findByUuid(req.getStudentUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with given UUID "));

        a.setMarkedAt(req.getMarkedAt());
        a.setMarkedBy(req.getMarkedBy());
        a.setAttendanceDate(req.getAttendanceDate());
        a.setStream(req.getStream());
        a.setGrade(req.getGrade());
        a.setStudent(student);
        a.setStatus(req.getStatus());

        attendanceRepo.save(a);
    }

    @Transactional
    public void deleteAttendance(UUID uuid) {
        DailyAttendance a = findEntity(uuid);
        attendanceRepo.delete(a);
    }
}