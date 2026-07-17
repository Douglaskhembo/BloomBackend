package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.PayrollSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollSettingsRepository extends JpaRepository<PayrollSettings, Long> {}
