package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.StatutoryDeduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatutoryDeductionRepository extends JpaRepository<StatutoryDeduction, Long> {
    boolean existsByName(String name);
}
