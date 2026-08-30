package com.bloom.bloomschool.gradeLevel.repository;

import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, UUID>{

    Optional<GradeEntity> findByUuid(UUID uuid);

    boolean existsByName(String name);

    List<GradeEntity> findAllByUuidIn(List<UUID> uuids);
}
