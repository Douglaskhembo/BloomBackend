package com.bloom.bloomschool.reports.service;

import com.bloom.bloomschool.assessments.entity.Assessment;
import com.bloom.bloomschool.assessments.entity.AssessmentMark;
import com.bloom.bloomschool.assessments.repository.AssessmentMarkRepository;
import com.bloom.bloomschool.assessments.repository.AssessmentRepository;
import com.bloom.bloomschool.attendance.entity.ClassTeacherAssignment;
import com.bloom.bloomschool.attendance.repository.ClassTeacherAssignmentRepository;
import com.bloom.bloomschool.reports.dto.response.SubjectScoreResponse;
import com.bloom.bloomschool.reports.dto.response.TermReportDetailResponse;
import com.bloom.bloomschool.reports.dto.response.TermReportResponse;
import com.bloom.bloomschool.reports.entity.ReportPublication;
import com.bloom.bloomschool.reports.repository.ReportPublicationRepository;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.school.repository.GradeLevelRepository;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TermReportService {

    private final StudentRepository studentRepo;
    private final AssessmentRepository assessmentRepo;
    private final AssessmentMarkRepository markRepo;
    private final GradeLevelRepository gradeLevelRepo;
    private final ReportPublicationRepository publicationRepo;
    private final ClassTeacherAssignmentRepository classTeacherRepo;

    private static final List<GradeBand> GRADE_BANDS = List.of(
            new GradeBand("A", 80, 100, 12, "Excellent"),
            new GradeBand("A-", 75, 79, 11, "Very Good"),
            new GradeBand("B+", 70, 74, 10, "Good"),
            new GradeBand("B", 65, 69, 9, "Fairly Good"),
            new GradeBand("B-", 60, 64, 8, "Good Average"),
            new GradeBand("C+", 55, 59, 7, "Average"),
            new GradeBand("C", 50, 54, 6, "Fair"),
            new GradeBand("C-", 45, 49, 5, "Below Average"),
            new GradeBand("D+", 40, 44, 4, "Below Average"),
            new GradeBand("D", 35, 39, 3, "Weak"),
            new GradeBand("D-", 30, 34, 2, "Very Weak"),
            new GradeBand("E", 0, 29, 1, "Very Poor")
    );

    public List<TermReportResponse> computeReports(UUID gradeLevelUuidFilter, String streamFilter, String term, int year, String search) {
        String gradeName = null;
        if (gradeLevelUuidFilter != null) {
            gradeName = gradeLevelRepo.findByUuid(gradeLevelUuidFilter)
                    .orElseThrow(() -> new EntityNotFoundException("Grade level not found"))
                    .getName();
        }
        List<Student> students = studentRepo.findActiveRoster(gradeName, streamFilter);
        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            students = students.stream()
                    .filter(s -> (s.getFirstName() + " " + s.getLastName() + " " + s.getAdmissionNumber()).toLowerCase().contains(q))
                    .toList();
        }

        Map<String, List<Student>> groups = students.stream()
                .collect(Collectors.groupingBy(s -> s.getGrade() + "|" + s.getStream()));

        List<TermReportResponse> result = new ArrayList<>();
        for (List<Student> group : groups.values()) result.addAll(rankGroup(group, term, year));
        return result;
    }

    /** Homeroom-scoped, for the teacher portal's "my class" term reports view. */
    public List<TermReportResponse> getMyClass(UUID teacherUuid, String term, int year) {
        ClassTeacherAssignment assignment = classTeacherRepo.findByTeacherUuid(teacherUuid).orElse(null);
        if (assignment == null) return List.of();
        List<Student> roster = studentRepo.findByGradeAndStreamAndStatus(
                assignment.getGradeLevel().getName(), assignment.getStream(), Student.Status.ACTIVE);
        return rankGroup(roster, term, year);
    }

    /** Parent-scoped — published reports only, never leaks draft rankings/scores. */
    public List<TermReportResponse> getMyChildren(UUID parentUserUuid, String term, int year) {
        List<Student> children = studentRepo.findByParentUserUuidAndStatus(parentUserUuid, Student.Status.ACTIVE);
        List<TermReportResponse> out = new ArrayList<>();
        for (Student child : children) {
            List<Student> group = studentRepo.findByGradeAndStreamAndStatus(child.getGrade(), child.getStream(), Student.Status.ACTIVE);
            rankGroup(group, term, year).stream()
                    .filter(r -> r.getStudentUuid().equals(child.getUuid()))
                    .filter(r -> "Published".equals(r.getStatus()))
                    .findFirst()
                    .ifPresent(out::add);
        }
        return out;
    }

    public TermReportDetailResponse getDetail(UUID studentUuid, String term, int year) {
        Student student = studentRepo.findByUuid(studentUuid)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        List<Assessment> assessments = assessmentRepo.findByGradeLevel_NameAndStreamAndTermAndYear(
                student.getGrade(), student.getStream(), term, year);
        List<AssessmentMark> marks = markRepo.findByAssessmentInAndStudent_Uuid(assessments, studentUuid);

        Map<UUID, String> subjectNames = new LinkedHashMap<>();
        Map<UUID, List<Double>> bySubject = new LinkedHashMap<>();
        for (AssessmentMark m : marks) {
            if (m.getScore() == null) continue;
            UUID subjectUuid = m.getAssessment().getSubject().getUuid();
            subjectNames.putIfAbsent(subjectUuid, m.getAssessment().getSubject().getName());
            bySubject.computeIfAbsent(subjectUuid, k -> new ArrayList<>()).add(m.getScore());
        }

        List<SubjectScoreResponse> subjects = new ArrayList<>();
        for (Map.Entry<UUID, List<Double>> e : bySubject.entrySet()) {
            double avg = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            GradeBand band = gradeBandFor(avg);
            subjects.add(SubjectScoreResponse.builder()
                    .subjectName(subjectNames.get(e.getKey()))
                    .score(round2(avg))
                    .grade(band.label)
                    .points(band.points)
                    .remark(band.remark)
                    .build());
        }

        List<Student> group = studentRepo.findByGradeAndStreamAndStatus(student.getGrade(), student.getStream(), Student.Status.ACTIVE);
        TermReportResponse ranked = rankGroup(group, term, year).stream()
                .filter(r -> r.getStudentUuid().equals(studentUuid))
                .findFirst()
                .orElse(null);

        return TermReportDetailResponse.builder()
                .studentUuid(student.getUuid())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .admissionNumber(student.getAdmissionNumber())
                .grade(student.getGrade())
                .stream(student.getStream())
                .term(term)
                .year(year)
                .meanScore(ranked != null ? ranked.getMeanScore() : 0)
                .position(ranked != null ? ranked.getPosition() : "-")
                .subjects(subjects)
                .build();
    }

    @Transactional
    public void publish(UUID gradeLevelUuid, String stream, String term, int year) {
        GradeLevel gradeLevel = gradeLevelRepo.findByUuid(gradeLevelUuid)
                .orElseThrow(() -> new EntityNotFoundException("Grade level not found"));
        ReportPublication pub = publicationRepo.findByGradeLevel_UuidAndStreamAndTermAndYear(gradeLevelUuid, stream, term, year)
                .orElseGet(() -> ReportPublication.builder().gradeLevel(gradeLevel).stream(stream).term(term).year(year).build());
        pub.setStatus(ReportPublication.Status.PUBLISHED);
        pub.setPublishedDate(new Date());
        publicationRepo.save(pub);
    }

    private List<TermReportResponse> rankGroup(List<Student> group, String term, int year) {
        record Scored(Student student, double mean) {}
        List<Scored> scored = new ArrayList<>();
        for (Student s : group) {
            Double mean = computeMeanScore(s, term, year);
            if (mean != null) scored.add(new Scored(s, mean));
        }
        scored.sort((a, b) -> Double.compare(b.mean(), a.mean()));

        UUID gradeLevelUuid = group.isEmpty() ? null
                : gradeLevelRepo.findByName(group.get(0).getGrade()).map(GradeLevel::getUuid).orElse(null);

        List<TermReportResponse> out = new ArrayList<>();
        int total = scored.size();
        for (int i = 0; i < total; i++) {
            Scored sc = scored.get(i);
            Student s = sc.student();
            out.add(TermReportResponse.builder()
                    .studentUuid(s.getUuid())
                    .studentName(s.getFirstName() + " " + s.getLastName())
                    .admissionNumber(s.getAdmissionNumber())
                    .gradeLevelUuid(gradeLevelUuid)
                    .grade(s.getGrade())
                    .stream(s.getStream())
                    .term(term)
                    .year(year)
                    .meanScore(round2(sc.mean()))
                    .position((i + 1) + "/" + total)
                    .status(publicationStatus(s.getGrade(), s.getStream(), term, year))
                    .build());
        }
        return out;
    }

    private Double computeMeanScore(Student student, String term, int year) {
        List<Assessment> assessments = assessmentRepo.findByGradeLevel_NameAndStreamAndTermAndYear(
                student.getGrade(), student.getStream(), term, year);
        if (assessments.isEmpty()) return null;
        List<AssessmentMark> marks = markRepo.findByAssessmentInAndStudent_Uuid(assessments, student.getUuid());

        Map<UUID, List<Double>> bySubject = new HashMap<>();
        for (AssessmentMark m : marks) {
            if (m.getScore() == null) continue;
            bySubject.computeIfAbsent(m.getAssessment().getSubject().getUuid(), k -> new ArrayList<>()).add(m.getScore());
        }
        if (bySubject.isEmpty()) return null;

        double sum = 0;
        for (List<Double> vals : bySubject.values()) sum += vals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        return sum / bySubject.size();
    }

    private String publicationStatus(String gradeName, String stream, String term, int year) {
        GradeLevel gradeLevel = gradeLevelRepo.findByName(gradeName).orElse(null);
        if (gradeLevel == null) return "Draft";
        return publicationRepo.findByGradeLevel_UuidAndStreamAndTermAndYear(gradeLevel.getUuid(), stream, term, year)
                .filter(p -> p.getStatus() == ReportPublication.Status.PUBLISHED)
                .map(p -> "Published")
                .orElse("Draft");
    }

    private GradeBand gradeBandFor(double score) {
        return GRADE_BANDS.stream()
                .filter(b -> score >= b.min && score <= b.max)
                .findFirst()
                .orElse(GRADE_BANDS.get(GRADE_BANDS.size() - 1));
    }

    private static double round2(double v) { return Math.round(v * 100) / 100.0; }

    private record GradeBand(String label, double min, double max, int points, String remark) {}
}
