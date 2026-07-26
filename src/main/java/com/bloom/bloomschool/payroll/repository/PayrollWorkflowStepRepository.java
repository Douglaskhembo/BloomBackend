package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.PayrollWorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PayrollWorkflowStepRepository extends JpaRepository<PayrollWorkflowStep, Long> {
    List<PayrollWorkflowStep> findAllByOrderBySequenceOrderAsc();
    List<PayrollWorkflowStep> findAllByActiveTrueOrderBySequenceOrderAsc();
    Optional<PayrollWorkflowStep> findByUuid(UUID uuid);
}
