package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.OtherDeduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtherDeductionRepository extends JpaRepository<OtherDeduction, Long> {
    boolean existsByName(String name);
}
