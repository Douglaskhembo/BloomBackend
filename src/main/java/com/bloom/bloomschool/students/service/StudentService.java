package com.bloom.bloomschool.students.service;

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

import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepo;
    private final AdmissionRepository admissionRepo;
    private final GradeLevelRepository gradeLevelRepo;

    // ── Students ─────────────────────────────────────────────────────────────

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
        s.setAdmissionNumber(generateAdmissionNumber());
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

    // ── Admissions ───────────────────────────────────────────────────────────

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
    public Admission updateAdmissionStage(UUID uuid, Stage stage) {
        Admission a = getAdmissionByUuid(uuid);
        a.setStage(stage);
        admissionRepo.save(a);
        if (stage == Stage.ENROLLED) enrollStudent(a);
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

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void enrollStudent(Admission a) {
        Student s = Student.builder()
                .admissionNumber(generateAdmissionNumber())
                .firstName(a.getFirstName())
                .lastName(a.getLastName())
                .gender(a.getGender())
                .dateOfBirth(a.getDateOfBirth())
                .address(a.getAddress())
                .medicalNotes(a.getMedicalNotes())
                .grade(a.getGrade())
                .stream(a.getStream())
                .gradeLevel(a.getGradeLevel())
                .parentName(a.getParentName())
                .parentPhone(a.getParentPhone())
                .parentEmail(a.getParentEmail())
                .admission(a)
                .status(Student.Status.ACTIVE)
                .build();
        studentRepo.save(s);
    }

    private String generateAdmissionNumber() {
        int year = Year.now().getValue();
        long count = studentRepo.count();
        return year + "/" + String.format("%04d", count + 1);
    }

    private GradeLevel resolveGradeLevel(UUID gradeLevelUuid) {
        if (gradeLevelUuid == null) return null;
        return gradeLevelRepo.findByUuid(gradeLevelUuid).orElse(null);
    }

    private Student buildStudent(Student s, StudentRequest req) {
        s.setFirstName(req.getFirstName());
        s.setLastName(req.getLastName());
        s.setGender(req.getGender());
        s.setDateOfBirth(req.getDateOfBirth());
        s.setAddress(req.getAddress());
        s.setMedicalNotes(req.getMedicalNotes());
        s.setGrade(req.getGrade());
        s.setStream(req.getStream());
        s.setGradeLevel(resolveGradeLevel(req.getGradeLevelUuid()));
        s.setParentName(req.getParentName());
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
        a.setAddress(req.getAddress());
        a.setMedicalNotes(req.getMedicalNotes());
        a.setGrade(req.getGrade());
        a.setStream(req.getStream());
        a.setGradeLevel(resolveGradeLevel(req.getGradeLevelUuid()));
        a.setParentName(req.getParentName());
        a.setParentRelationship(req.getParentRelationship());
        a.setParentPhone(req.getParentPhone());
        a.setParentEmail(req.getParentEmail());
        return a;
    }
}
