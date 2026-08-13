package com.bloom.bloomschool.timetable.service;

import com.bloom.bloomschool.timetable.dto.request.TimetablePeriodRequest;
import com.bloom.bloomschool.timetable.entity.TimetablePeriod;
import com.bloom.bloomschool.timetable.repository.TimetableEntryRepository;
import com.bloom.bloomschool.timetable.repository.TimetablePeriodRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimetablePeriodService {

    private final TimetablePeriodRepository periodRepo;
    private final TimetableEntryRepository entryRepo;

    public List<TimetablePeriod> getAll() {
        return periodRepo.findAllByOrderByStartTimeAsc();
    }

    @Transactional
    public TimetablePeriod create(TimetablePeriodRequest req) {
        validate(req, null);
        return periodRepo.save(TimetablePeriod.builder()
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .breakPeriod(req.isBreakPeriod())
                .breakLabel(req.isBreakPeriod() ? req.getBreakLabel() : null)
                .build());
    }

    @Transactional
    public TimetablePeriod update(UUID uuid, TimetablePeriodRequest req) {
        TimetablePeriod p = periodRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Time slot not found"));
        validate(req, uuid);
        p.setStartTime(req.getStartTime());
        p.setEndTime(req.getEndTime());
        p.setBreakPeriod(req.isBreakPeriod());
        p.setBreakLabel(req.isBreakPeriod() ? req.getBreakLabel() : null);
        return periodRepo.save(p);
    }

    @Transactional
    public void delete(UUID uuid) {
        TimetablePeriod p = periodRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Time slot not found"));
        if (entryRepo.existsByPeriod_Uuid(uuid))
            throw new IllegalArgumentException("Cannot delete a time slot that has timetable entries assigned; unassign them first");
        periodRepo.deleteById(p.getId());
    }

    private void validate(TimetablePeriodRequest req, UUID excludeUuid) {
        if (!req.getStartTime().isBefore(req.getEndTime()))
            throw new IllegalArgumentException("Start time must be before end time");
        for (TimetablePeriod other : periodRepo.findAll()) {
            if (excludeUuid != null && other.getUuid().equals(excludeUuid)) continue;
            boolean overlaps = req.getStartTime().isBefore(other.getEndTime()) && other.getStartTime().isBefore(req.getEndTime());
            if (overlaps)
                throw new IllegalArgumentException("This time range overlaps an existing slot (" + other.getStartTime() + " - " + other.getEndTime() + ")");
        }
    }
}
