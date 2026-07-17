package com.bloom.bloomschool.school.repository;

import com.bloom.bloomschool.school.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByCode(String code);
    boolean existsByStatus(Department.Status status);
}
