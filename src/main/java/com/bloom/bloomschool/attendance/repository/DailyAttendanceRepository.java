package com.bloom.bloomschool.attendance.repository;

import com.bloom.bloomschool.attendance.entity.DailyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyAttendanceRepository extends JpaRepository<DailyAttendance, Long> {

   Optional<DailyAttendance> findByUuid(UUID uuid);

   List<DailyAttendance> findByGradeAndStream(String grade, String stream);

   List<DailyAttendance> findByStudentAdmissionNumber(String admissionNumber);

   List<DailyAttendance> findByGradeAndStreamAndAttendanceDate(
           String grade,
           String stream,
           LocalDateTime attendanceDate
   );

   boolean existsByGradeAndStreamAndAttendanceDate(
           String grade,
           String stream,
           LocalDateTime attendanceDate
   );

   @Query("SELECT COUNT(a) > 0 FROM DailyAttendance a WHERE a.student.uuid = :studentUuid AND a.attendanceDate = :attendanceDate")
   boolean existsByStudentUuidAndAttendanceDate(
           @Param("studentUuid") UUID studentUuid,
           @Param("attendanceDate") LocalDateTime attendanceDate
   );
   List<DailyAttendance> findByStudentUuid(UUID studentUuid);

}