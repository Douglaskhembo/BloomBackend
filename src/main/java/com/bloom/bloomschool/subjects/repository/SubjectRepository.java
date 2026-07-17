package com.bloom.bloomschool.subjects.repository;

import com.bloom.bloomschool.subjects.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByName(String name);
    List<Subject> findByGrade(String grade);
}
