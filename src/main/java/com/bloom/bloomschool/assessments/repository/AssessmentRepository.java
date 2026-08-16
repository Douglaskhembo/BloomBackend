package com.bloom.bloomschool.assessments.repository;

import com.bloom.bloomschool.assessments.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    Optional<Assessment> findByUuid(UUID uuid);
    List<Assessment> findByTeacher_UuidAndGradeLevel_UuidAndStreamAndSubject_Uuid(
            UUID teacherUuid, UUID gradeLevelUuid, String stream, UUID subjectUuid);
    List<Assessment> findByGradeLevel_NameAndStreamAndTermAndYear(String gradeName, String stream, String term, int year);
    /** Every assessment for a class+subject regardless of who created it — used when a class
     *  teacher or admin (not the assessment's own creator) browses performance for a class. */
    List<Assessment> findByGradeLevel_UuidAndStreamAndSubject_Uuid(UUID gradeLevelUuid, String stream, UUID subjectUuid);
    boolean existsBySubject_UuidAndGradeLevel_UuidAndStreamAndTermAndYearAndName(
            UUID subjectUuid, UUID gradeLevelUuid, String stream, String term, int year, String name);
    long countByTermAndYear(String term, int year);
}
