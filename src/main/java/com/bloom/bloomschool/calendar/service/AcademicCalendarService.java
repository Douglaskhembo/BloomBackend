package com.bloom.bloomschool.calendar.service;

import com.bloom.bloomschool.calendar.dto.CurrentTermResponse;
import com.bloom.bloomschool.calendar.dto.SchoolEventRequest;
import com.bloom.bloomschool.calendar.dto.TermPeriodRequest;
import com.bloom.bloomschool.calendar.entity.SchoolEvent;
import com.bloom.bloomschool.calendar.entity.TermPeriod;
import com.bloom.bloomschool.calendar.repository.SchoolEventRepository;
import com.bloom.bloomschool.calendar.repository.TermPeriodRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcademicCalendarService {

    private final TermPeriodRepository termPeriodRepo;
    private final SchoolEventRepository schoolEventRepo;

    // ── Term Periods ─────────────────────────────────────────────────────────

    public List<TermPeriod> getAllTermPeriods() {
        return termPeriodRepo.findAllByOrderByAcademicYearDescTermAsc();
    }

    /** Which academic year/term today falls inside, per the configured term periods — null/null
     *  when nothing's been configured for the current date, so callers know not to auto-default. */
    public CurrentTermResponse getCurrentTerm() {
        LocalDate today = LocalDate.now();
        return termPeriodRepo.findFirstByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today)
                .map(tp -> new CurrentTermResponse(tp.getAcademicYear(), tp.getTerm()))
                .orElse(new CurrentTermResponse(null, null));
    }

    @Transactional
    public TermPeriod createTermPeriod(TermPeriodRequest req) {
        validateRange(req.getStartDate(), req.getEndDate());
        if (termPeriodRepo.findByAcademicYearAndTerm(req.getAcademicYear(), req.getTerm()).isPresent())
            throw new IllegalArgumentException(req.getTerm() + " " + req.getAcademicYear() + " is already configured");
        TermPeriod period = TermPeriod.builder()
                .academicYear(req.getAcademicYear())
                .term(req.getTerm())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .build();
        validateNoOverlap(period, null);
        return termPeriodRepo.save(period);
    }

    @Transactional
    public TermPeriod updateTermPeriod(Long id, TermPeriodRequest req) {
        validateRange(req.getStartDate(), req.getEndDate());
        TermPeriod period = termPeriodRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Term period not found"));
        termPeriodRepo.findByAcademicYearAndTerm(req.getAcademicYear(), req.getTerm())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new IllegalArgumentException(req.getTerm() + " " + req.getAcademicYear() + " is already configured"); });
        period.setAcademicYear(req.getAcademicYear());
        period.setTerm(req.getTerm());
        period.setStartDate(req.getStartDate());
        period.setEndDate(req.getEndDate());
        validateNoOverlap(period, id);
        return termPeriodRepo.save(period);
    }

    @Transactional
    public void deleteTermPeriod(Long id) {
        termPeriodRepo.deleteById(id);
    }

    private void validateRange(LocalDate start, LocalDate end) {
        if (end.isBefore(start))
            throw new IllegalArgumentException("End date cannot be before start date");
    }

    /** Terms within the same academic year must not overlap — a maker/approver auto-default can
     *  only ever mean one term at a time. */
    private void validateNoOverlap(TermPeriod candidate, Long excludingId) {
        for (TermPeriod other : termPeriodRepo.findByAcademicYear(candidate.getAcademicYear())) {
            if (excludingId != null && other.getId().equals(excludingId)) continue;
            if (other.getTerm().equals(candidate.getTerm())) continue;
            boolean overlaps = !candidate.getStartDate().isAfter(other.getEndDate())
                    && !other.getStartDate().isAfter(candidate.getEndDate());
            if (overlaps)
                throw new IllegalArgumentException(candidate.getTerm() + " overlaps with " + other.getTerm() + " (" + other.getStartDate() + " – " + other.getEndDate() + ")");
        }
    }

    // ── School Events ────────────────────────────────────────────────────────

    public List<SchoolEvent> getAllEvents() {
        return schoolEventRepo.findAllByOrderByStartDateAsc();
    }

    @Transactional
    public SchoolEvent createEvent(SchoolEventRequest req) {
        validateRange(req.getStartDate(), req.getEndDate());
        return schoolEventRepo.save(SchoolEvent.builder()
                .name(req.getName())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .active(req.isActive())
                .build());
    }

    @Transactional
    public SchoolEvent updateEvent(Long id, SchoolEventRequest req) {
        validateRange(req.getStartDate(), req.getEndDate());
        SchoolEvent event = schoolEventRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("School event not found"));
        event.setName(req.getName());
        event.setStartDate(req.getStartDate());
        event.setEndDate(req.getEndDate());
        event.setActive(req.isActive());
        return schoolEventRepo.save(event);
    }

    @Transactional
    public SchoolEvent toggleEvent(Long id) {
        SchoolEvent event = schoolEventRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("School event not found"));
        event.setActive(!event.isActive());
        return schoolEventRepo.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        schoolEventRepo.deleteById(id);
    }
}
