package com.bloom.bloomschool.attendance.controller;

import com.bloom.bloomschool.attendance.service.AttendanceReportService;
import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/attendance/reports")
@RequiredArgsConstructor
public class AttendanceReportController {

    private final AttendanceReportService reportService;
    private final PermissionResolver permissionResolver;

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<?>> searchStudents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String stream,
            @RequestParam(required = false) String admissionNumber) {
        permissionResolver.requirePermission("ATTENDANCE_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(reportService.searchStudents(from, to, grade, stream, admissionNumber)));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<?>> studentSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String stream) {
        permissionResolver.requirePermission("ATTENDANCE_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(reportService.getStudentAttendanceSummary(from, to, grade, stream)));
    }

    @GetMapping("/staff")
    public ResponseEntity<ApiResponse<?>> searchStaff(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String staffId) {
        permissionResolver.requirePermission("ATTENDANCE_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(reportService.searchStaff(from, to, staffId)));
    }

    @GetMapping("/my-class")
    public ResponseEntity<ApiResponse<?>> getMyClass(
            @RequestParam UUID teacherUuid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getMyClassAttendance(teacherUuid, from, to)));
    }

    @GetMapping("/my-children")
    public ResponseEntity<ApiResponse<?>> getMyChildren(
            @RequestParam UUID parentUserUuid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getMyChildrenAttendance(parentUserUuid, from, to)));
    }
}
