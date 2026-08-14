package com.bloom.bloomschool.assessments.service;

import com.bloom.bloomschool.assessments.dto.request.AssessmentRequest;
import com.bloom.bloomschool.assessments.dto.request.MarkEntryRequest;
import com.bloom.bloomschool.assessments.dto.response.AssessmentResponse;
import com.bloom.bloomschool.assessments.dto.response.MyClassResponse;
import com.bloom.bloomschool.assessments.dto.response.StudentMarkRowResponse;
import com.bloom.bloomschool.assessments.entity.Assessment;
import com.bloom.bloomschool.assessments.entity.AssessmentMark;
import com.bloom.bloomschool.assessments.repository.AssessmentMarkRepository;
import com.bloom.bloomschool.assessments.repository.AssessmentRepository;
import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.auth.repo.UserRepository;
import com.bloom.bloomschool.common.utils.UserUtils;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.school.repository.GradeLevelRepository;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import com.bloom.bloomschool.subjects.entity.Subject;
import com.bloom.bloomschool.subjects.repository.SubjectRepository;
import com.bloom.bloomschool.timetable.entity.TimetableEntry;
import com.bloom.bloomschool.timetable.repository.TimetableEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepo;
    private final AssessmentMarkRepository markRepo;
    private final TimetableEntryRepository entryRepo;
    private final StudentRepository studentRepo;
    private final StaffRepository staffRepo;
    private final SubjectRepository subjectRepo;
    private final GradeLevelRepository gradeLevelRepo;
    private final UserRepository userRepo;
    private final UserUtils userUtils;

    public List<MyClassResponse> getMyClasses(UUID teacherUuid) {
        List<TimetableEntry> entries = entryRepo.findByTeacher_Uuid(teacherUuid);
        Map<String, MyClassResponse> byKey = new LinkedHashMap<>();
        for (TimetableEntry e : entries) {
            String key = e.getGradeLevel().getUuid() + "|" + e.getStream() + "|" + e.getSubject().getUuid();
            if (byKey.containsKey(key)) continue;
            long count = studentRepo.countByGradeAndStreamAndStatus(e.getGradeLevel().getName(), e.getStream(), Student.Status.ACTIVE);
            byKey.put(key, MyClassResponse.builder()
                    .gradeLevelUuid(e.getGradeLevel().getUuid())
                    .grade(e.getGradeLevel().getName())
                    .stream(e.getStream())
                    .subjectUuid(e.getSubject().getUuid())
                    .subjectName(e.getSubject().getName())
                    .studentCount(count)
                    .build());
        }
        return new ArrayList<>(byKey.values());
    }

    public List<StudentMarkRowResponse> getRoster(UUID gradeLevelUuid, String stream) {
        GradeLevel gradeLevel = gradeLevelRepo.findByUuid(gradeLevelUuid)
                .orElseThrow(() -> new EntityNotFoundException("Grade level not found"));
        return studentRepo.findByGradeAndStreamAndStatus(gradeLevel.getName(), stream, Student.Status.ACTIVE).stream()
                .map(s -> StudentMarkRowResponse.builder()
                        .studentUuid(s.getUuid())
                        .admissionNumber(s.getAdmissionNumber())
                        .studentName(s.getFirstName() + " " + s.getLastName())
                        .score(null)
                        .build())
                .toList();
    }

    public List<AssessmentResponse> getMyAssessments(UUID teacherUuid, UUID gradeLevelUuid, String stream, UUID subjectUuid) {
        return assessmentRepo.findByTeacher_UuidAndGradeLevel_UuidAndStreamAndSubject_Uuid(teacherUuid, gradeLevelUuid, stream, subjectUuid)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AssessmentResponse createAssessment(AssessmentRequest req) {
        Staff teacher = resolveCurrentTeacher();
        Subject subject = subjectRepo.findByUuid(req.getSubjectUuid())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        GradeLevel gradeLevel = gradeLevelRepo.findByUuid(req.getGradeLevelUuid())
                .orElseThrow(() -> new EntityNotFoundException("Grade level not found"));
        String stream = req.getStream() == null ? "" : req.getStream();

        requireOwnsClass(teacher, subject.getUuid(), gradeLevel.getUuid(), stream);

        if (assessmentRepo.existsBySubject_UuidAndGradeLevel_UuidAndStreamAndTermAndYearAndName(
                subject.getUuid(), gradeLevel.getUuid(), stream, req.getTerm(), req.getYear(), req.getName()))
            throw new IllegalArgumentException("An assessment with this name already exists for this class, subject and term");

        Assessment assessment = Assessment.builder()
                .name(req.getName())
                .type(req.getType())
                .term(req.getTerm())
                .year(req.getYear())
                .subject(subject)
                .gradeLevel(gradeLevel)
                .stream(stream)
                .teacher(teacher)
                .maxScore(req.getMaxScore() == null ? 100 : req.getMaxScore())
                .build();
        return toResponse(assessmentRepo.save(assessment));
    }

    public List<StudentMarkRowResponse> getMarks(UUID assessmentUuid) {
        Assessment assessment = assessmentRepo.findByUuid(assessmentUuid)
                .orElseThrow(() -> new EntityNotFoundException("Assessment not found"));
        List<Student> roster = studentRepo.findByGradeAndStreamAndStatus(
                assessment.getGradeLevel().getName(), assessment.getStream(), Student.Status.ACTIVE);
        Map<UUID, Double> scores = markRepo.findByAssessment_Uuid(assessmentUuid).stream()
                .collect(Collectors.toMap(m -> m.getStudent().getUuid(), AssessmentMark::getScore));
        return roster.stream()
                .map(s -> StudentMarkRowResponse.builder()
                        .studentUuid(s.getUuid())
                        .admissionNumber(s.getAdmissionNumber())
                        .studentName(s.getFirstName() + " " + s.getLastName())
                        .score(scores.get(s.getUuid()))
                        .build())
                .toList();
    }

    @Transactional
    public void saveMarks(UUID assessmentUuid, MarkEntryRequest req) {
        Assessment assessment = assessmentRepo.findByUuid(assessmentUuid)
                .orElseThrow(() -> new EntityNotFoundException("Assessment not found"));
        Staff teacher = resolveCurrentTeacher();
        if (!assessment.getTeacher().getUuid().equals(teacher.getUuid()))
            throw new AccessDeniedException("You can only enter marks for assessments you created");

        for (MarkEntryRequest.Entry entry : req.getEntries()) {
            Student student = studentRepo.findByUuid(entry.getStudentUuid())
                    .orElseThrow(() -> new EntityNotFoundException("Student not found: " + entry.getStudentUuid()));
            if (entry.getScore() != null && (entry.getScore() < 0 || entry.getScore() > assessment.getMaxScore()))
                throw new IllegalArgumentException("Score for " + student.getAdmissionNumber() + " must be between 0 and " + assessment.getMaxScore());

            AssessmentMark mark = markRepo.findByAssessment_UuidAndStudent_Uuid(assessmentUuid, entry.getStudentUuid())
                    .orElseGet(() -> AssessmentMark.builder().assessment(assessment).student(student).build());
            mark.setScore(entry.getScore());
            markRepo.save(mark);
        }
    }

    @Transactional
    public void deleteAssessment(UUID uuid) {
        Assessment assessment = assessmentRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Assessment not found"));
        Staff teacher = resolveCurrentTeacher();
        if (!assessment.getTeacher().getUuid().equals(teacher.getUuid()))
            throw new AccessDeniedException("You can only delete assessments you created");
        markRepo.deleteAll(markRepo.findByAssessment_Uuid(uuid));
        assessmentRepo.delete(assessment);
    }

    private void requireOwnsClass(Staff teacher, UUID subjectUuid, UUID gradeLevelUuid, String stream) {
        boolean owns = entryRepo.findByTeacher_Uuid(teacher.getUuid()).stream()
                .anyMatch(e -> e.getSubject().getUuid().equals(subjectUuid)
                        && e.getGradeLevel().getUuid().equals(gradeLevelUuid)
                        && e.getStream().equals(stream));
        if (!owns) throw new AccessDeniedException("You are not timetabled to teach this subject to this class");
    }

    private Staff resolveCurrentTeacher() {
        String username = userUtils.getCurrentUser();
        if (username == null) throw new IllegalStateException("Not authenticated");
        User user = userRepo.findByUserName(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + username));
        String profileRef = user.getProfileRef();
        if (profileRef == null || profileRef.isBlank())
            throw new EntityNotFoundException("No staff profile linked to this account");
        return staffRepo.findByUuid(UUID.fromString(profileRef))
                .orElseThrow(() -> new EntityNotFoundException("Linked staff record not found"));
    }

    private AssessmentResponse toResponse(Assessment a) {
        long studentCount = studentRepo.countByGradeAndStreamAndStatus(a.getGradeLevel().getName(), a.getStream(), Student.Status.ACTIVE);
        long gradedCount = markRepo.findByAssessment_Uuid(a.getUuid()).stream().filter(m -> m.getScore() != null).count();
        return AssessmentResponse.builder()
                .uuid(a.getUuid())
                .name(a.getName())
                .type(a.getType())
                .term(a.getTerm())
                .year(a.getYear())
                .subjectUuid(a.getSubject().getUuid())
                .subjectName(a.getSubject().getName())
                .gradeLevelUuid(a.getGradeLevel().getUuid())
                .grade(a.getGradeLevel().getName())
                .stream(a.getStream())
                .maxScore(a.getMaxScore())
                .studentCount(studentCount)
                .gradedCount(gradedCount)
                .build();
    }
}
