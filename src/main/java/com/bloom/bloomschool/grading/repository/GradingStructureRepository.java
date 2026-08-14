package com.bloom.bloomschool.grading.repository;

import com.bloom.bloomschool.grading.entity.GradingStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradingStructureRepository extends JpaRepository<GradingStructure, Long> {
    Optional<GradingStructure> findByUuid(UUID uuid);
    boolean existsByGrade(String grade);
    List<GradingStructure> findAllByOrderByGradeAsc();
}
