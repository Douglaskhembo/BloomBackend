package com.bloom.bloomschool.subject.repository;

import com.bloom.bloomschool.subject.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, UUID> {
    Optional<SubjectEntity> findByUuid(UUID uuid);

    boolean existsByName(String name);

    boolean existsBySubjectCode(String subjectCode);

    boolean existsByNameAndUuidNot(String name, UUID uuid);

    boolean existsBySubjectCodeAndUuidNot(String subjectCode, UUID uuid);
    @Query(value = """
    SELECT s.uuid, s.name, s.subject_code, s.is_active,
           g.uuid, g.name, g.display_order, g.capacity, g.active, g.streams
    FROM bloom_school_subject s
    LEFT JOIN bloom_subject_grade_levels sg ON sg.subject_uuid = s.uuid
    LEFT JOIN bloom_school_grade g ON g.uuid = sg.grade_uuid
    """, nativeQuery = true)
    List<Object[]> findAllSubjects();

    @Query(value = """
    SELECT s.uuid, s.name, s.subject_code, s.is_active,
           g.uuid, g.name, g.display_order, g.capacity, g.active, g.streams
    FROM bloom_school_subject s
    LEFT JOIN bloom_subject_grade_levels sg ON sg.subject_uuid = s.uuid
    LEFT JOIN bloom_school_grade g ON g.uuid = sg.grade_uuid
    WHERE s.uuid = :UUID
    """, nativeQuery = true)
    List<Object[]> findSubjectByUuid(@Param("UUID") UUID uuid);
}
