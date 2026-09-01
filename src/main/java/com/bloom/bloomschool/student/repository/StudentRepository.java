package com.bloom.bloomschool.student.repository;

import com.bloom.bloomschool.student.entity.Student;
import com.bloom.bloomschool.student.entity.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{
    Optional<Student> findByUuid(UUID uuid);

    Optional<Student> findByAdmissionNumber(String admissionNumber);

    Optional<Student> findByStudentEmail(String studentEmail);

    Optional<Student> findByEntryNumber(String entryNumber);

    List<Student> findAll();

    List<Student> findByStatus(StudentStatus status);

    boolean existsByAdmissionNumber(String admissionNumber);

    boolean existsByEntryNumber(String entryNumber);
}
