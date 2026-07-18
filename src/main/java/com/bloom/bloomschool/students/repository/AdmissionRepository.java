package com.bloom.bloomschool.students.repository;

import com.bloom.bloomschool.students.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    Optional<Admission> findByUuid(UUID uuid);
    Optional<Admission> findByApplicationId(String applicationId);
    long count();
}
