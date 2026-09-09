package com.bloom.bloomschool.attendance.controller;

import com.bloom.bloomschool.attendance.dto.DailyAttendanceRequestDTO;
import com.bloom.bloomschool.attendance.dto.DailyAttendanceResponseDTO;
import com.bloom.bloomschool.attendance.service.DailyAttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class DailyAttendanceController {
    private final DailyAttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<DailyAttendanceResponseDTO>> getAllAttendances() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<DailyAttendanceResponseDTO> getAttendanceByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(attendanceService.getAttendanceByUuid(uuid));
    }

    @GetMapping("/student/{studentUuid}")
    public ResponseEntity<List<DailyAttendanceResponseDTO>> getAttendancesByStudentUuid(@PathVariable UUID studentUuid) {
        return ResponseEntity.ok(attendanceService.getAttendancesByStudentUuid(studentUuid));
    }

    @GetMapping("/student/admission/{admissionNumber}")
    public ResponseEntity<List<DailyAttendanceResponseDTO>> getAttendancesByStudentAdmission(@PathVariable String admissionNumber) {
        return ResponseEntity.ok(attendanceService.getAttendancesByStudentAdmission(admissionNumber));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<DailyAttendanceResponseDTO>> getAttendancesByGradeAndStream(
            @RequestParam String grade,
            @RequestParam String stream) {
        return ResponseEntity.ok(attendanceService.getAttendancesByGradeAndStream(grade, stream));
    }

    @PostMapping
    public ResponseEntity<?> createAttendance(@RequestBody DailyAttendanceRequestDTO requestDTO) {
        attendanceService.createAttendance(requestDTO);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateAttendance(@PathVariable UUID uuid, @RequestBody DailyAttendanceRequestDTO requestDTO) {
        attendanceService.updateAttendance(uuid, requestDTO);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteAttendance(@PathVariable UUID uuid) {
        attendanceService.deleteAttendance(uuid);
        return ResponseEntity.noContent().build();
    }
}
