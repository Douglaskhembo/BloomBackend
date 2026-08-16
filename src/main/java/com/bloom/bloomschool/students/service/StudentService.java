package com.bloom.bloomschool.students.service;

import com.bloom.bloomschool.fees.service.FeeService;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.school.repository.GradeLevelRepository;
import com.bloom.bloomschool.students.dto.AdmissionRequest;
import com.bloom.bloomschool.students.dto.StudentRequest;
import com.bloom.bloomschool.students.entity.Admission;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.AdmissionRepository;
import com.bloom.bloomschool.students.repository.StudentRepository;
import com.bloom.bloomschool.students.util.Stage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepo;
    private final AdmissionRepository admissionRepo;
    private final GradeLevelRepository gradeLevelRepo;
    private final FeeService feeService;

    public List<Student> getAll(String search) {
        if (search != null && !search.isBlank()) return studentRepo.search(search.trim());
        return studentRepo.findAll();
    }

    public Student getByUuid(UUID uuid) {
        return studentRepo.findByUuid(uuid).orElseThrow(() -> new EntityNotFoundException("Student not found"));
    }

    @Transactional
    public Student create(StudentRequest req) {
        Student s = buildStudent(new Student(), req);
        assertCapacity(s.getGradeLevel(), s.getGrade(), s.getStream());
        s.setAdmissionNumber(generateAdmissionNumber());
        if (s.getJoinDate() == null) s.setJoinDate(LocalDate.now());
        return studentRepo.save(s);
    }

    @Transactional
    public Student update(UUID uuid, StudentRequest req) {
        return studentRepo.save(buildStudent(getByUuid(uuid), req));
    }

    @Transactional
    public Student updateStatus(UUID uuid, Student.Status status) {
        Student s = getByUuid(uuid);
        s.setStatus(status);
        return studentRepo.save(s);
    }

    @Transactional
    public void delete(UUID uuid) {
        Student s = getByUuid(uuid);
        studentRepo.deleteById(s.getId());
    }

    public List<Student> getMyChildren(UUID parentUserUuid) {
        return studentRepo.findByParentUserUuidAndStatus(parentUserUuid, Student.Status.ACTIVE);
    }

    @Transactional
    public Student linkParent(UUID studentUuid, UUID parentUserUuid) {
        Student s = getByUuid(studentUuid);
        s.setParentUserUuid(parentUserUuid);
        return studentRepo.save(s);
    }

    public List<Admission> getAllAdmissions() {
        return admissionRepo.findAll();
    }

    public Admission getAdmissionByUuid(UUID uuid) {
        return admissionRepo.findByUuid(uuid).orElseThrow(() -> new EntityNotFoundException("Application not found"));
    }

    @Transactional
    public Admission createAdmission(AdmissionRequest req) {
        long count = admissionRepo.count();
        Admission a = buildAdmission(new Admission(), req);
        a.setApplicationId("APP-" + String.format("%03d", count + 1));
        a.setStage(Stage.APPLICATION_REVIEW);
        return admissionRepo.save(a);
    }

    @Transactional
    public Admission updateAdmissionStage(UUID uuid, Stage stage, LocalDate joinDate) {
        Admission a = getAdmissionByUuid(uuid);
        if (stage == Stage.ENROLLED && !feeService.hasConfirmedPayment(a.getUuid()))
            throw new IllegalArgumentException("Cannot enrol " + a.getFirstName() + " " + a.getLastName()
                    + " — no confirmed fee payment has been recorded for this application yet");
        a.setStage(stage);
        admissionRepo.save(a);
        if (stage == Stage.ENROLLED) enrollStudent(a, joinDate);
        return a;
    }

    @Transactional
    public Admission updateAdmission(UUID uuid, AdmissionRequest req) {
        return admissionRepo.save(buildAdmission(getAdmissionByUuid(uuid), req));
    }

    @Transactional
    public void deleteAdmission(UUID uuid) {
        Admission a = getAdmissionByUuid(uuid);
        admissionRepo.deleteById(a.getId());
    }

    private void enrollStudent(Admission a, LocalDate joinDate) {
        assertCapacity(a.getGradeLevel(), a.getGrade(), a.getStream());
        Student s = Student.builder()
                .admissionNumber(generateAdmissionNumber())
                .firstName(a.getFirstName())
                .lastName(a.getLastName())
                .gender(a.getGender())
                .dateOfBirth(a.getDateOfBirth())
                .birthCertificateNumber(a.getBirthCertificateNumber())
                .address(a.getAddress())
                .medicalNotes(a.getMedicalNotes())
                .grade(a.getGrade())
                .stream(normalizeStream(a.getStream()))
                .joinDate(joinDate != null ? joinDate : LocalDate.now())
                .boarderStatus(a.getBoarderStatus())
                .gradeLevel(a.getGradeLevel())
                .parentName(a.getParentName())
                .parentRelationship(a.getParentRelationship())
                .parentPhone(a.getParentPhone())
                .parentEmail(a.getParentEmail())
                .admission(a)
                .status(Student.Status.ACTIVE)
                .build();
        studentRepo.save(s);
        feeService.adoptAdmissionPayments(a.getUuid(), s.getAdmissionNumber(), s.getFirstName() + " " + s.getLastName(), s.getGrade(), s.getStream());
    }

    private String generateAdmissionNumber() {
        long max = studentRepo.findAllAdmissionNumbers().stream()
                .filter(a -> a != null && a.matches("\\d+"))
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0L);
        return String.valueOf(max + 1);
    }

    private GradeLevel resolveGradeLevel(UUID gradeLevelUuid) {
        if (gradeLevelUuid == null) return null;
        return gradeLevelRepo.findByUuid(gradeLevelUuid).orElse(null);
    }

    private void assertCapacity(GradeLevel gradeLevel, String grade, String stream) {
        if (gradeLevel == null) return;
        Integer capacity = capacityFor(gradeLevel, stream);
        if (capacity == null) return;
        long current = (stream == null || stream.isBlank())
                ? studentRepo.countByGradeAndStatus(grade, Student.Status.ACTIVE)
                : studentRepo.countByGradeAndStreamAndStatus(grade, stream, Student.Status.ACTIVE);
        if (current >= capacity) {
            String where = (stream == null || stream.isBlank()) ? grade : grade + " · " + stream;
            throw new IllegalArgumentException(where + " is at full capacity (" + capacity + " student" + (capacity == 1 ? "" : "s") + ")");
        }
    }

    private Integer capacityFor(GradeLevel gradeLevel, String stream) {
        if (gradeLevel.getStreams() <= 1) return gradeLevel.getCapacity();
        List<String> names = gradeLevel.getStreamNames();
        List<Integer> caps = gradeLevel.getStreamCapacities();
        int idx = names.indexOf(stream);
        if (idx < 0 || idx >= caps.size()) return null;
        return caps.get(idx);
    }

    private Student buildStudent(Student s, StudentRequest req) {
        s.setFirstName(req.getFirstName());
        s.setLastName(req.getLastName());
        s.setGender(req.getGender());
        s.setDateOfBirth(req.getDateOfBirth());
        s.setBirthCertificateNumber(req.getBirthCertificateNumber());
        s.setAddress(req.getAddress());
        s.setMedicalNotes(req.getMedicalNotes());
        s.setGrade(req.getGrade());
        s.setStream(normalizeStream(req.getStream()));
        if (req.getJoinDate() != null) s.setJoinDate(req.getJoinDate());
        s.setBoarderStatus(req.getBoarderStatus());
        s.setGradeLevel(resolveGradeLevel(req.getGradeLevelUuid()));
        s.setParentName(req.getParentName());
        s.setParentRelationship(req.getParentRelationship());
        s.setParentPhone(req.getParentPhone());
        s.setParentEmail(req.getParentEmail());
        if (req.getStatus() != null) s.setStatus(req.getStatus());
        return s;
    }

    private Admission buildAdmission(Admission a, AdmissionRequest req) {
        a.setFirstName(req.getFirstName());
        a.setLastName(req.getLastName());
        a.setGender(req.getGender());
        a.setDateOfBirth(req.getDateOfBirth());
        a.setBirthCertificateNumber(req.getBirthCertificateNumber());
        a.setAddress(req.getAddress());
        a.setMedicalNotes(req.getMedicalNotes());
        a.setGrade(req.getGrade());
        a.setStream(normalizeStream(req.getStream()));
        a.setBoarderStatus(req.getBoarderStatus());
        a.setGradeLevel(resolveGradeLevel(req.getGradeLevelUuid()));
        a.setParentName(req.getParentName());
        a.setParentRelationship(req.getParentRelationship());
        a.setParentPhone(req.getParentPhone());
        a.setParentEmail(req.getParentEmail());
        return a;
    }

    /** "" for a streamless grade, never null — same convention TimetableEntry/ClassTeacherAssignment
     *  already use for their own `stream` column, so an exact-match lookup against those never
     *  silently misses a student just because this one came in null from the request. */
    private String normalizeStream(String stream) {
        return stream == null ? "" : stream;
    }
}
