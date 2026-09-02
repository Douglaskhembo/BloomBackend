package com.bloom.bloomschool.calender.repository;

import com.bloom.bloomschool.calender.entity.SchoolEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolEventRepository extends JpaRepository<SchoolEvent, Long>{
    Optional<SchoolEvent> findByUuid(UUID uuid);

    //List<SchoolEvent> findAll();

    List<SchoolEvent> findByStartDate(LocalDate startDate);

    @Query("SELECT COUNT(e) > 0 FROM SchoolEvent e WHERE e.eventName = ?1 AND e.startDate = ?2")
    boolean existsByEventNameAndStartDate(String eventName, LocalDate startDate);
}