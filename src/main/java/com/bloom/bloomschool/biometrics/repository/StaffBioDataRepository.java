package com.bloom.bloomschool.biometrics.repository;

import com.bloom.bloomschool.biometrics.entity.StaffBioData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StaffBioDataRepository extends JpaRepository<StaffBioData, Long> {
    Optional<StaffBioData> findByUuid(UUID uuid);
    Optional<StaffBioData> findByStaffId(Long staffId);
    boolean existsByStaffId(Long staffId);
}
