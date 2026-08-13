package com.bloom.bloomschool.timetable.service;

import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.school.repository.GradeLevelRepository;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.staff.util.StaffType;
import com.bloom.bloomschool.subjects.entity.Subject;
import com.bloom.bloomschool.subjects.repository.SubjectRepository;
import com.bloom.bloomschool.timetable.dto.request.TimetableEntryRequest;
import com.bloom.bloomschool.timetable.dto.response.AvailableTeacherResponse;
import com.bloom.bloomschool.timetable.dto.response.TimetableEntryResponse;
import com.bloom.bloomschool.timetable.entity.TimetableEntry;
import com.bloom.bloomschool.timetable.entity.TimetablePeriod;
import com.bloom.bloomschool.timetable.repository.TimetableEntryRepository;
import com.bloom.bloomschool.timetable.repository.TimetablePeriodRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimetableEntryService {

    private final TimetableEntryRepository entryRepo;
    private final TimetablePeriodRepository periodRepo;
    private final GradeLevelRepository gradeLevelRepo;
    private final SubjectRepository subjectRepo;
    private final StaffRepository staffRepo;

    public List<TimetableEntryResponse> getGrid(UUID gradeLevelUuid, String stream) {
        return entryRepo.findByGradeLevel_UuidAndStream(gradeLevelUuid, normalize(stream)).stream()
                .map(this::toResponse).toList();
    }

    public List<TimetableEntryResponse> getMine(UUID teacherUuid) {
        return entryRepo.findByTeacher_Uuid(teacherUuid).stream().map(this::toResponse).toList();
    }

    public List<AvailableTeacherResponse> getAvailableTeachers(UUID subjectUuid, DayOfWeek dayOfWeek, UUID periodUuid, UUID excludeEntryUuid) {
        Subject subject = subjectRepo.findByUuid(subjectUuid)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));

        Set<Long> busyTeacherIds = entryRepo.findByDayOfWeekAndPeriod_Uuid(dayOfWeek, periodUuid).stream()
                .filter(e -> excludeEntryUuid == null || !e.getUuid().equals(excludeEntryUuid))
                .map(e -> e.getTeacher().getId())
                .collect(Collectors.toSet());

        return staffRepo.findByStaffType(StaffType.TEACHING).stream()
                .filter(s -> s.getSubjects().stream().anyMatch(sub -> sub.getUuid().equals(subject.getUuid())))
                .filter(s -> !busyTeacherIds.contains(s.getId()))
                .map(s -> AvailableTeacherResponse.builder()
                        .teacherUuid(s.getUuid())
                        .teacherName(s.getFirstName() + " " + s.getLastName())
                        .staffId(s.getStaffId())
                        .build())
                .toList();
    }

    @Transactional
    public TimetableEntryResponse assign(TimetableEntryRequest req) {
        GradeLevel gradeLevel = gradeLevelRepo.findByUuid(req.getGradeLevelUuid())
                .orElseThrow(() -> new EntityNotFoundException("Grade level not found"));
        TimetablePeriod period = periodRepo.findByUuid(req.getPeriodUuid())
                .orElseThrow(() -> new EntityNotFoundException("Time slot not found"));
        if (period.isBreakPeriod())
            throw new IllegalArgumentException("Cannot assign a subject to a break period");
        Subject subject = subjectRepo.findByUuid(req.getSubjectUuid())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        Staff teacher = staffRepo.findByUuid(req.getTeacherUuid())
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        String stream = normalize(req.getStream());
        TimetableEntry existing = entryRepo.findByGradeLevel_UuidAndStreamAndDayOfWeekAndPeriod_Uuid(
                req.getGradeLevelUuid(), stream, req.getDayOfWeek(), req.getPeriodUuid()).orElse(null);

        // Authoritative server-side conflict check — the frontend's available-teachers dropdown
        // already filters this out, but that's UX sugar, not the source of truth.
        TimetableEntry conflict = entryRepo.findByDayOfWeekAndPeriod_Uuid(req.getDayOfWeek(), req.getPeriodUuid()).stream()
                .filter(e -> e.getTeacher().getId().equals(teacher.getId()))
                .filter(e -> existing == null || !e.getId().equals(existing.getId()))
                .findFirst().orElse(null);
        if (conflict != null) {
            throw new IllegalArgumentException(teacher.getFirstName() + " " + teacher.getLastName()
                    + " is already teaching " + conflict.getSubject().getName() + " for "
                    + conflict.getGradeLevel().getName() + " " + conflict.getStream() + " at this time");
        }

        TimetableEntry entry = existing != null ? existing : new TimetableEntry();
        entry.setGradeLevel(gradeLevel);
        entry.setStream(stream);
        entry.setDayOfWeek(req.getDayOfWeek());
        entry.setPeriod(period);
        entry.setSubject(subject);
        entry.setTeacher(teacher);
        return toResponse(entryRepo.save(entry));
    }

    @Transactional
    public void unassign(UUID uuid) {
        TimetableEntry entry = entryRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Timetable entry not found"));
        entryRepo.deleteById(entry.getId());
    }

    private String normalize(String stream) {
        return stream == null ? "" : stream;
    }

    private TimetableEntryResponse toResponse(TimetableEntry e) {
        return TimetableEntryResponse.builder()
                .uuid(e.getUuid())
                .gradeLevelUuid(e.getGradeLevel().getUuid())
                .grade(e.getGradeLevel().getName())
                .stream(e.getStream())
                .dayOfWeek(e.getDayOfWeek())
                .periodUuid(e.getPeriod().getUuid())
                .subjectUuid(e.getSubject().getUuid())
                .subjectName(e.getSubject().getName())
                .teacherUuid(e.getTeacher().getUuid())
                .teacherName(e.getTeacher().getFirstName() + " " + e.getTeacher().getLastName())
                .build();
    }
}
