package com.bloom.bloomschool.school.repository;

import com.bloom.bloomschool.school.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByUuid(UUID uuid);
    boolean existsByCode(String code);
    boolean existsByStatus(Branch.Status status);
}
