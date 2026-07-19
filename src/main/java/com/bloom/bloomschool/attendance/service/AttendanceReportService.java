package com.bloom.bloomschool.attendance.service;

import com.bloom.bloomschool.attendance.entity.ClassTeacherAssignment;
import com.bloom.bloomschool.attendance.repository.ClassTeacherAssignmentRepository;
import com.bloom.bloomschool.biometrics.dto.response.AttendanceResponse;
import com.bloom.bloomschool.biometrics.entity.StaffAttendance;
import com.bloom.bloomschool.biometrics.entity.StudentAttendance;
import com.bloom.bloomschool.biometrics.repository.StaffAttendanceRepository;
import com.bloom.bloomschool.biometrics.repository.StudentAttendanceRepository;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Read/reporting layer over the existing staff & student attendance records — filtered
 * search for the admin Attendance page, plus the scoped "my class" (class teacher) and
 * "my children" (parent) views.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceReportService {

    private final StaffAttendanceRepository staffAttendanceRepo;
    private final StudentAttendanceRepository studentAttendanceRepo;
    private final StudentRepository studentRepository;
    private final ClassTeacherAssignmentRepository classTeacherRepo;

    public List<AttendanceResponse> searchStudents(LocalDate from, LocalDate to, String grade, String stream, String admissionNumber) {
        return studentAttendanceRepo.search(from, to, blank(grade), blank(stream), blank(admissionNumber))
                .stream().map(this::toResponse).toList();
    }

    public List<AttendanceResponse> searchStaff(LocalDate from, LocalDate to, String staffId) {
        return staffAttendanceRepo.search(from, to, blank(staffId)).stream().map(this::toResponse).toList();
    }

    public List<AttendanceResponse> getMyClassAttendance(UUID teacherUuid, LocalDate from, LocalDate to) {
        ClassTeacherAssignment a = classTeacherRepo.findByTeacherUuid(teacherUuid)
                .orElseThrow(() -> new EntityNotFoundException("No class assigned to this teacher"));
        return searchStudents(from, to, a.getGrade(), a.getStream(), null);
    }

    /** Only ever the parent's ACTIVE children — left/graduated students are never included. */
    public List<AttendanceResponse> getMyChildrenAttendance(UUID parentUserUuid, LocalDate from, LocalDate to) {
        List<String> admissionNumbers = studentRepository.findByParentUserUuidAndStatus(parentUserUuid, Student.Status.ACTIVE)
                .stream().map(Student::getAdmissionNumber).toList();
        if (admissionNumbers.isEmpty()) return List.of();
        return studentAttendanceRepo.findByAdmissionNumbersAndDateRange(admissionNumbers, from, to)
                .stream().map(this::toResponse).toList();
    }

    private String blank(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }

    private AttendanceResponse toResponse(StudentAttendance a) {
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

    private AttendanceResponse toResponse(StaffAttendance a) {
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
