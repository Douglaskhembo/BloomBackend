package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.StaffSalary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffSalaryRepository extends JpaRepository<StaffSalary, Long> {
    Optional<StaffSalary> findByStaffId(String staffId);
    boolean existsByStaffId(String staffId);
}
