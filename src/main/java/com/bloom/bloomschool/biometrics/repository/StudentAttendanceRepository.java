package com.bloom.bloomschool.biometrics.repository;

import com.bloom.bloomschool.biometrics.entity.StudentAttendance;
import com.bloom.bloomschool.biometrics.util.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {
    Optional<StudentAttendance> findByUuid(UUID uuid);

    List<StudentAttendance> findByStudentIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Long studentId, LocalDate from, LocalDate to);

    List<StudentAttendance> findByAttendanceDateOrderByStudentId(LocalDate date);

    /** Latest open entry (no exit yet) for a student on a given date */
    @Query("SELECT a FROM StudentAttendance a WHERE a.student.id = :studentId AND a.attendanceDate = :date AND a.exitTime IS NULL ORDER BY a.entryTime DESC")
    Optional<StudentAttendance> findOpenEntry(Long studentId, LocalDate date);

    long countByAttendanceDateAndStatus(LocalDate date, AttendanceStatus status);

    @Query("SELECT a FROM StudentAttendance a WHERE a.attendanceDate BETWEEN :from AND :to " +
            "AND (:grade IS NULL OR a.student.grade = :grade) " +
            "AND (:stream IS NULL OR a.student.stream = :stream) " +
            "AND (:admissionNumber IS NULL OR a.student.admissionNumber = :admissionNumber) " +
            "ORDER BY a.attendanceDate DESC, a.entryTime DESC")
    List<StudentAttendance> search(LocalDate from, LocalDate to, String grade, String stream, String admissionNumber);

    @Query("SELECT a FROM StudentAttendance a WHERE a.attendanceDate BETWEEN :from AND :to " +
            "AND a.student.admissionNumber IN :admissionNumbers ORDER BY a.attendanceDate DESC, a.entryTime DESC")
    List<StudentAttendance> findByAdmissionNumbersAndDateRange(List<String> admissionNumbers, LocalDate from, LocalDate to);
}
