package com.bloom.bloomschool.feestructure.repository;

import com.bloom.bloomschool.feestructure.entity.FeeStructureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructureEntity, UUID>{
    Optional<FeeStructureEntity> findByUuid(UUID uuid);

    boolean existsByAcademicYearAndGradeAndTerm(int academicYear, String grade, String term);
    boolean existsByAcademicYearAndGradeAndTermAndUuidNot(int academicYear, String grade, String term, UUID uuid);
}
