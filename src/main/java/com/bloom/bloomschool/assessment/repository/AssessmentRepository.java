package com.bloom.bloomschool.assessment.repository;

import com.bloom.bloomschool.assessment.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {
    Optional<Assessment> findByUuid(UUID uuid);

    boolean existsByNameAndTermAndYearAndSubjectUuidAndGradeUuid(
            String name, String term, int year, UUID subjectUuid, UUID gradeUuid);

    boolean existsByNameAndTermAndYearAndSubjectUuidAndGradeUuidAndUuidNot(
            String name, String term, int year, UUID subjectUuid, UUID gradeUuid, UUID uuid);
}
