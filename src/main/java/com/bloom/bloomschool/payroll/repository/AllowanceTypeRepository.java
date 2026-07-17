package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.AllowanceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllowanceTypeRepository extends JpaRepository<AllowanceType, Long> {
    boolean existsByName(String name);
}
