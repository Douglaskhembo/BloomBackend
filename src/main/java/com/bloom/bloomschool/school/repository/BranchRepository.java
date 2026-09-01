package com.bloom.bloomschool.school.repository;

import com.bloom.bloomschool.school.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByUuid(UUID uuid);

    @Query("SELECT COUNT(b) > 0 FROM Branch b WHERE b.branchCode = ?1")
    boolean existsByCode(String branchCode);

    @Query("SELECT COUNT(b) > 0 FROM Branch b WHERE b.branchName = ?1")
    boolean existsByName(String branchName);

    @Query("SELECT COUNT(b) > 0 FROM Branch b WHERE b.phone = ?1")
    boolean existsByPhone(String phone);
}