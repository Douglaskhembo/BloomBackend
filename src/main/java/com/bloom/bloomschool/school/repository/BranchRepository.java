package com.bloom.bloomschool.school.repository;

import com.bloom.bloomschool.school.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    boolean existsByCode(String code);
    boolean existsByStatus(Branch.Status status);
}
