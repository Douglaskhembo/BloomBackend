package com.bloom.bloomschool.subject.repository;

import com.bloom.bloomschool.subject.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, UUID> {
    Optional<SubjectEntity> findByUuid(UUID uuid);

    boolean existsByName(String name);

    boolean existsBySubjectCode(String subjectCode);
}
