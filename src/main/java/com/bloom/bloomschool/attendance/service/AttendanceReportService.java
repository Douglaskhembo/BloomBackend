package com.bloom.bloomschool.attendance.service;

import com.bloom.bloomschool.attendance.dto.response.AttendanceSummaryResponse;
import com.bloom.bloomschool.attendance.entity.ClassTeacherAssignment;
import com.bloom.bloomschool.attendance.repository.ClassTeacherAssignmentRepository;
import com.bloom.bloomschool.biometrics.dto.response.AttendanceResponse;
import com.bloom.bloomschool.biometrics.entity.StaffAttendance;
import com.bloom.bloomschool.biometrics.entity.StudentAttendance;
import com.bloom.bloomschool.biometrics.repository.StaffAttendanceRepository;
import com.bloom.bloomschool.biometrics.repository.StudentAttendanceRepository;
import com.bloom.bloomschool.biometrics.util.EventType;
import com.bloom.bloomschool.common.util.LeaveDayCounter;
import com.bloom.bloomschool.holidays.service.HolidayService;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final HolidayService holidayService;

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
        return searchStudents(from, to, a.getGradeLevel().getName(), a.getStream(), null);
    }

    /** Only ever the parent's ACTIVE children — left/graduated students are never included. */
    public List<AttendanceResponse> getMyChildrenAttendance(UUID parentUserUuid, LocalDate from, LocalDate to) {
        List<String> admissionNumbers = studentRepository.findByParentUserUuidAndStatus(parentUserUuid, Student.Status.ACTIVE)
                .stream().map(Student::getAdmissionNumber).toList();
        if (admissionNumbers.isEmpty()) return List.of();
        return studentAttendanceRepo.findByAdmissionNumbersAndDateRange(admissionNumbers, from, to)
                .stream().map(this::toResponse).toList();
    }

    /**
     * Percentage present per student — roster comes from {@code Student}, not from attendance
     * rows, so a student with zero scans in range still shows up at 0% rather than being
     * silently omitted. "Present" = at least one ENTRY scan on a school day (weekends and
     * persisted public holidays excluded from both the denominator and eligible presence dates).
     */
    public List<AttendanceSummaryResponse> getStudentAttendanceSummary(LocalDate from, LocalDate to, String grade, String stream) {
        String g = blank(grade), s = blank(stream);
        List<Student> roster = studentRepository.findActiveRoster(g, s);
        if (roster.isEmpty()) return List.of();

        List<StudentAttendance> events = studentAttendanceRepo.search(from, to, g, s, null);
        Set<LocalDate> holidayDates = holidayService.getActiveHolidayDates();
        long totalSchoolDays = LeaveDayCounter.countDays(from, to, true, true, holidayDates);

        Map<Long, Set<LocalDate>> presentDatesByStudentId = new HashMap<>();
        for (StudentAttendance a : events) {
            if (a.getEventType() != EventType.ENTRY
                    || LeaveDayCounter.isWeekend(a.getAttendanceDate())
                    || holidayDates.contains(a.getAttendanceDate())) continue;
            presentDatesByStudentId.computeIfAbsent(a.getStudent().getId(), k -> new HashSet<>()).add(a.getAttendanceDate());
        }

        return roster.stream().map(student -> {
            long daysPresent = presentDatesByStudentId.getOrDefault(student.getId(), Set.of()).size();
            return AttendanceSummaryResponse.builder()
                    .admissionNumber(student.getAdmissionNumber())
                    .name(student.getFirstName() + " " + student.getLastName())
                    .grade(student.getGrade())
                    .stream(student.getStream())
                    .daysPresent(daysPresent)
                    .totalSchoolDays(totalSchoolDays)
                    .percentage(totalSchoolDays > 0 ? (daysPresent * 100.0 / totalSchoolDays) : 0)
                    .build();
        }).sorted(Comparator.comparingDouble(AttendanceSummaryResponse::getPercentage)).toList();
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
