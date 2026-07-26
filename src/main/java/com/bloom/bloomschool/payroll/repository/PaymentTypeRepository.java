package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
    boolean existsByCode(String code);
    Optional<PaymentType> findByUuid(UUID uuid);
}
