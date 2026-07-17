package com.bloom.bloomschool.biometrics.repository;

import com.bloom.bloomschool.biometrics.entity.StudentBioData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentBioDataRepository extends JpaRepository<StudentBioData, Long> {
    Optional<StudentBioData> findByUuid(UUID uuid);
    Optional<StudentBioData> findByStudentId(Long studentId);
    boolean existsByStudentId(Long studentId);
}
