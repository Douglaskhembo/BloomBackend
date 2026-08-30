package com.bloom.bloomschool.gradeLevel.repository;

import com.bloom.bloomschool.gradeLevel.entity.GradingStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeStructureRepository extends JpaRepository<GradingStructure, UUID> {

    Optional<GradingStructure> findByUuid(UUID uuid);

    boolean existsByGrade(String grade);
}
