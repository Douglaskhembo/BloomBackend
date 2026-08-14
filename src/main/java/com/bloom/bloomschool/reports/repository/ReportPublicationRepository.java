package com.bloom.bloomschool.reports.repository;

import com.bloom.bloomschool.reports.entity.ReportPublication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReportPublicationRepository extends JpaRepository<ReportPublication, Long> {
    Optional<ReportPublication> findByGradeLevel_UuidAndStreamAndTermAndYear(UUID gradeLevelUuid, String stream, String term, int year);
}
