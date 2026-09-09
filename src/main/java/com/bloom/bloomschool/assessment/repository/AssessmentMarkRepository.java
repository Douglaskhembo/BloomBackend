package com.bloom.bloomschool.assessment.repository;

import com.bloom.bloomschool.assessment.entity.AssessmentMarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssessmentMarkRepository extends JpaRepository<AssessmentMarks, UUID > {
}
