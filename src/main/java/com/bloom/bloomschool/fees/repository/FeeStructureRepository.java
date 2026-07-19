package com.bloom.bloomschool.fees.repository;

import com.bloom.bloomschool.fees.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    Optional<FeeStructure> findByUuid(UUID uuid);
    long countByGradeAndTermAndStatus(String grade, String term, FeeStructure.Status status);
    Optional<FeeStructure> findFirstByGradeAndTermAndStatusOrderByReviewedAtDesc(String grade, String term, FeeStructure.Status status);
    List<FeeStructure> findAllByOrderBySubmittedAtDesc();
}
