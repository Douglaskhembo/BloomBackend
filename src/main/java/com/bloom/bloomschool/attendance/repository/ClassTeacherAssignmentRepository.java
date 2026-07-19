package com.bloom.bloomschool.attendance.repository;

import com.bloom.bloomschool.attendance.entity.ClassTeacherAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ClassTeacherAssignmentRepository extends JpaRepository<ClassTeacherAssignment, Long> {
    Optional<ClassTeacherAssignment> findByUuid(UUID uuid);
    Optional<ClassTeacherAssignment> findByTeacherId(Long teacherId);
    Optional<ClassTeacherAssignment> findByGradeAndStream(String grade, String stream);
    boolean existsByGradeAndStream(String grade, String stream);

    @Query("SELECT a FROM ClassTeacherAssignment a WHERE a.teacher.uuid = :teacherUuid")
    Optional<ClassTeacherAssignment> findByTeacherUuid(UUID teacherUuid);
}
