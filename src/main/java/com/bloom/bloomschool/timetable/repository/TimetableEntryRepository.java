package com.bloom.bloomschool.timetable.repository;

import com.bloom.bloomschool.timetable.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
    Optional<TimetableEntry> findByUuid(UUID uuid);
    Optional<TimetableEntry> findByGradeLevel_UuidAndStreamAndDayOfWeekAndPeriod_Uuid(
            UUID gradeLevelUuid, String stream, DayOfWeek dayOfWeek, UUID periodUuid);
    List<TimetableEntry> findByGradeLevel_UuidAndStream(UUID gradeLevelUuid, String stream);
    List<TimetableEntry> findByTeacher_Uuid(UUID teacherUuid);
    List<TimetableEntry> findByDayOfWeekAndPeriod_Uuid(DayOfWeek dayOfWeek, UUID periodUuid);
    boolean existsByPeriod_Uuid(UUID periodUuid);
}
