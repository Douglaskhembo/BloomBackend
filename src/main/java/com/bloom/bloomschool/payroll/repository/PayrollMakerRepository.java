package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.PayrollMaker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PayrollMakerRepository extends JpaRepository<PayrollMaker, Long> {
    List<PayrollMaker> findAll();
    Optional<PayrollMaker> findByUuid(UUID uuid);
    boolean existsByUserUuid(UUID userUuid);
    boolean existsByUserId(Long userId);
}
