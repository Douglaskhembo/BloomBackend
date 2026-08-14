package com.bloom.bloomschool.holidays.service;

import com.bloom.bloomschool.holidays.dto.HolidayRequest;
import com.bloom.bloomschool.holidays.entity.Holiday;
import com.bloom.bloomschool.holidays.repository.HolidayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HolidayService {

    private final HolidayRepository holidayRepo;

    public List<Holiday> getAll() {
        return holidayRepo.findAll();
    }

    /** Dates of every active holiday, for use by the Leave module's day-counting logic. */
    public Set<LocalDate> getActiveHolidayDates() {
        return holidayRepo.findAll().stream()
                .filter(Holiday::isActive)
                .map(Holiday::getDate)
                .collect(Collectors.toSet());
    }

    @Transactional
    public Holiday create(HolidayRequest req) {
        if (holidayRepo.existsByDate(req.getDate()))
            throw new IllegalArgumentException("A holiday is already recorded on " + req.getDate());
        return holidayRepo.save(Holiday.builder()
                .name(req.getName())
                .date(req.getDate())
                .recurringAnnually(req.isRecurringAnnually())
                .active(req.isActive())
                .build());
    }

    @Transactional
    public Holiday update(Long id, HolidayRequest req) {
        Holiday h = holidayRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Holiday not found"));
        if (!h.getDate().equals(req.getDate()) && holidayRepo.existsByDate(req.getDate()))
            throw new IllegalArgumentException("A holiday is already recorded on " + req.getDate());
        h.setName(req.getName());
        h.setDate(req.getDate());
        h.setRecurringAnnually(req.isRecurringAnnually());
        h.setActive(req.isActive());
        return holidayRepo.save(h);
    }

    @Transactional
    public Holiday toggle(Long id) {
        Holiday h = holidayRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Holiday not found"));
        h.setActive(!h.isActive());
        return holidayRepo.save(h);
    }

    @Transactional
    public void delete(Long id) {
        holidayRepo.deleteById(id);
    }
}
