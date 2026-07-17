package com.bloom.bloomschool.biometrics.repository;

import com.bloom.bloomschool.biometrics.entity.StaffAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance, Long> {
    Optional<StaffAttendance> findByUuid(UUID uuid);

    List<StaffAttendance> findByStaffIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Long staffId, LocalDate from, LocalDate to);

    List<StaffAttendance> findByAttendanceDateOrderByStaffId(LocalDate date);

    /** Latest open clock-in (no clock-out yet) for a staff on a given date */
    @Query("SELECT a FROM StaffAttendance a WHERE a.staff.id = :staffId AND a.attendanceDate = :date AND a.clockOut IS NULL ORDER BY a.clockIn DESC")
    Optional<StaffAttendance> findOpenClockIn(Long staffId, LocalDate date);

    // long countByAttendanceDateAndStatus(LocalDate date, StaffAttendance.AttendanceStatus status);
}
