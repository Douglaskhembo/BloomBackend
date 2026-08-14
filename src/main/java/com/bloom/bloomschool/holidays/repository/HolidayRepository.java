package com.bloom.bloomschool.holidays.repository;

import com.bloom.bloomschool.holidays.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    boolean existsByDate(LocalDate date);
    Optional<Holiday> findByUuid(UUID uuid);
}
