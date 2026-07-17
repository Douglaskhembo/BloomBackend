package com.bloom.bloomschool.students.service;

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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepo;
    private final AdmissionRepository admissionRepo;

    // ── Students ─────────────────────────────────────────────────────────────

    public List<Student> getAll(String search) {
        if (search != null && !search.isBlank()) return studentRepo.search(search.trim());
        return studentRepo.findAll();
    }

    public Student getById(Long id) {
        return studentRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Student not found"));
    }

    @Transactional
    public Student create(StudentRequest req) {
        String admNo = generateAdmissionNumber();
        Student s = buildStudent(new Student(), req);
        s.setAdmissionNumber(admNo);
        return studentRepo.save(s);
    }

    @Transactional
    public Student update(Long id, StudentRequest req) {
        Student s = getById(id);
        return studentRepo.save(buildStudent(s, req));
    }

    @Transactional
    public Student updateStatus(Long id, Student.Status status) {
        Student s = getById(id);
        s.setStatus(status);
        return studentRepo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        studentRepo.deleteById(id);
    }

    // ── Admissions ───────────────────────────────────────────────────────────

    public List<Admission> getAllAdmissions() {
        return admissionRepo.findAll();
    }

    public Admission getAdmissionById(Long id) {
        return admissionRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Application not found"));
    }

    @Transactional
    public Admission createAdmission(AdmissionRequest req) {
        long count = admissionRepo.count();
        String appId = "APP-" + String.format("%03d", count + 1);
        Admission a = buildAdmission(new Admission(), req);
        a.setApplicationId(appId);
        a.setStage(Stage.APPLICATION_REVIEW);
        return admissionRepo.save(a);
    }

    @Transactional
    public Admission updateAdmissionStage(Long id, Stage stage) {
        Admission a = getAdmissionById(id);
        a.setStage(stage);
        admissionRepo.save(a);

        if (stage == Stage.ENROLLED) {
            enrollStudent(a);
        }
        return a;
    }

    @Transactional
    public Admission updateAdmission(Long id, AdmissionRequest req) {
        Admission a = getAdmissionById(id);
        return admissionRepo.save(buildAdmission(a, req));
    }

    @Transactional
    public void deleteAdmission(Long id) {
        admissionRepo.deleteById(id);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void enrollStudent(Admission a) {
        String admNo = generateAdmissionNumber();
        Student s = Student.builder()
                .admissionNumber(admNo)
                .firstName(a.getFirstName())
                .lastName(a.getLastName())
                .gender(a.getGender())
                .dateOfBirth(a.getDateOfBirth())
                .address(a.getAddress())
                .medicalNotes(a.getMedicalNotes())
                .grade(a.getGrade())
                .stream(a.getStream())
                .parentName(a.getParentName())
                .parentPhone(a.getParentPhone())
                .parentEmail(a.getParentEmail())
                .status(Student.Status.ACTIVE)
                .build();
        studentRepo.save(s);
    }

    private String generateAdmissionNumber() {
        int year = Year.now().getValue();
        long count = studentRepo.count();
        return year + "/" + String.format("%04d", count + 1);
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
        a.setParentName(req.getParentName());
        a.setParentRelationship(req.getParentRelationship());
        a.setParentPhone(req.getParentPhone());
        a.setParentEmail(req.getParentEmail());
        return a;
    }
}
