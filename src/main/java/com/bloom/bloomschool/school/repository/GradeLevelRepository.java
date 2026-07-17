package com.bloom.bloomschool.school.repository;

import com.bloom.bloomschool.school.entity.GradeLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeLevelRepository extends JpaRepository<GradeLevel, Long> {
    List<GradeLevel> findAllByOrderByDisplayOrderAsc();
    boolean existsByName(String name);
}
