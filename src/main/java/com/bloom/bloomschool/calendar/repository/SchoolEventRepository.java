package com.bloom.bloomschool.calendar.repository;

import com.bloom.bloomschool.calendar.entity.SchoolEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SchoolEventRepository extends JpaRepository<SchoolEvent, Long> {
    List<SchoolEvent> findAllByOrderByStartDateAsc();
    Optional<SchoolEvent> findByUuid(UUID uuid);
}
