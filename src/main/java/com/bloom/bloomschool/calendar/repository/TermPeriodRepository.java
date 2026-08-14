package com.bloom.bloomschool.calendar.repository;

import com.bloom.bloomschool.calendar.entity.TermPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TermPeriodRepository extends JpaRepository<TermPeriod, Long> {
    List<TermPeriod> findAllByOrderByAcademicYearDescTermAsc();
    List<TermPeriod> findByAcademicYear(Integer academicYear);
    Optional<TermPeriod> findByAcademicYearAndTerm(Integer academicYear, String term);
    Optional<TermPeriod> findByUuid(UUID uuid);
    Optional<TermPeriod> findFirstByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate date1, LocalDate date2);
}
