package com.bloom.bloomschool.timetable.repository;

import com.bloom.bloomschool.timetable.entity.TimetablePeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimetablePeriodRepository extends JpaRepository<TimetablePeriod, Long> {
    Optional<TimetablePeriod> findByUuid(UUID uuid);
    List<TimetablePeriod> findAllByOrderByStartTimeAsc();
}
