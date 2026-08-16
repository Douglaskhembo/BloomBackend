package com.bloom.bloomschool.assessments.repository;

import com.bloom.bloomschool.assessments.entity.Assessment;
import com.bloom.bloomschool.assessments.entity.AssessmentMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentMarkRepository extends JpaRepository<AssessmentMark, Long> {
    List<AssessmentMark> findByAssessment_Uuid(UUID assessmentUuid);
    Optional<AssessmentMark> findByAssessment_UuidAndStudent_Uuid(UUID assessmentUuid, UUID studentUuid);
    List<AssessmentMark> findByAssessmentInAndStudent_UuidIn(Collection<Assessment> assessments, Collection<UUID> studentUuids);
    List<AssessmentMark> findByAssessmentInAndStudent_Uuid(Collection<Assessment> assessments, UUID studentUuid);

    @Query("SELECT m FROM AssessmentMark m WHERE m.assessment.term = :term AND m.assessment.year = :year AND m.score IS NOT NULL")
    List<AssessmentMark> findGradedByTermAndYear(@Param("term") String term, @Param("year") int year);
}
