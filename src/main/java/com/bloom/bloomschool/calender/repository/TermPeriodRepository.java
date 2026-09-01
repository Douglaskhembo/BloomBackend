package com.bloom.bloomschool.calender.repository;

import com.bloom.bloomschool.calender.entity.TermPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TermPeriodRepository extends JpaRepository<TermPeriod, Long> {
    Optional<TermPeriod> findByUuid(UUID uuid);

    @Query("SELECT COUNT(t) > 0 FROM TermPeriod t WHERE t.term = :term AND t.academicYear = :academicYear")
    boolean existsByTermAndAcademicYear(@Param("term") String term, @Param("academicYear") Integer academicYear);
}