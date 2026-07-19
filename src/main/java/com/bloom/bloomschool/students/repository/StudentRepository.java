package com.bloom.bloomschool.students.repository;

import com.bloom.bloomschool.students.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUuid(UUID uuid);
    Optional<Student> findByAdmissionNumber(String admissionNumber);
    boolean existsByAdmissionNumber(String admissionNumber);
    List<Student> findByGrade(String grade);
    List<Student> findByParentUserUuidAndStatus(UUID parentUserUuid, Student.Status status);
    List<Student> findByGradeAndStreamAndStatus(String grade, String stream, Student.Status status);
    long count();

    @Query("SELECT s FROM Student s WHERE LOWER(CONCAT(s.firstName,' ',s.lastName,' ',s.admissionNumber)) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Student> search(String q);
}
