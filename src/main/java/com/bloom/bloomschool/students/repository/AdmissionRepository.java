package com.bloom.bloomschool.students.repository;

import com.bloom.bloomschool.students.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    Optional<Admission> findByApplicationId(String applicationId);
    long count();
}
