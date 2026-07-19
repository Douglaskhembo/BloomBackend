package com.bloom.bloomschool.fees.repository;

import com.bloom.bloomschool.fees.entity.FeeStructureAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeStructureAuditRepository extends JpaRepository<FeeStructureAudit, Long> {
    List<FeeStructureAudit> findAllByOrderByAtDesc();
}
