package com.bloom.bloomschool.attendance.repository;

import com.bloom.bloomschool.attendance.entity.BiometricDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BiometricDeviceRepository extends JpaRepository<BiometricDevice, Long> {
    Optional<BiometricDevice> findByUuid(UUID uuid);
    Optional<BiometricDevice> findByDeviceCode(String deviceCode);
    boolean existsByDeviceCode(String deviceCode);
}
