package com.bloom.bloomschool.school.repository;

import com.bloom.bloomschool.school.entity.GradeLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GradeLevelRepository extends JpaRepository<GradeLevel, Long> {
    Optional<GradeLevel> findByUuid(UUID uuid);
    List<GradeLevel> findAllByOrderByDisplayOrderAsc();
    List<GradeLevel> findAllByUuidIn(Set<UUID> uuids);
    boolean existsByName(String name);
}
