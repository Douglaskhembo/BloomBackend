package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {
    List<PayrollRun> findAllByOrderByProcessedAtDesc();
    boolean existsByYearAndMonthIndex(int year, int monthIndex);
}
