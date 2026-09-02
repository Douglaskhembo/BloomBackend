package com.bloom.bloomschool.student.service;

import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.student.dto.*;
import com.bloom.bloomschool.student.entity.Student;
import com.bloom.bloomschool.student.entity.StudentStatus;
import com.bloom.bloomschool.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepo;

    private Student findEntity(UUID uuid) {
        return studentRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }

    public List<StudentResponseDTO> getAllStudents() {
        return studentRepo.findAll().stream()
                .map(s -> StudentResponseDTO.builder()
                        .uuid(s.getUuid())
                        .admissionNumber(s.getAdmissionNumber())
                        .fullName(s.getFirstName() + " " + s.getLastName())
                        .studentEmail(s.getStudentEmail())
                        .entryNumber(s.getEntryNumber())
                        .dateOfBirth(s.getDateOfBirth())
                        .status(s.getStatus() != null ? s.getStatus().name() : "N/A")
                        .build())
                .toList();
    }

    public StudentResponseDTO getStudentByUuid(UUID uuid) {
        Student s = findEntity(uuid);
        return StudentResponseDTO.builder()
                .uuid(s.getUuid())
                .admissionNumber(s.getAdmissionNumber())
                .fullName(s.getFirstName() + " " + s.getLastName())
                .studentEmail(s.getStudentEmail())
                .entryNumber(s.getEntryNumber())
                .dateOfBirth(s.getDateOfBirth())
                .status(s.getStatus().name())
                .build();
    }

    @Transactional
    public void createStudent(StudentRequestDTO req) {
        if (studentRepo.existsByAdmissionNumber(req.getAdmissionNumber())) {
            throw new RuntimeException("Student with this admission number already exists!!");
        }
        if (studentRepo.existsByEntryNumber(req.getEntryNumber())){
            throw new RuntimeException("Student with this entry number already exists!!");
        }
                 studentRepo.save(Student.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .admissionNumber(req.getAdmissionNumber())
                .studentEmail(req.getStudentEmail())
                .entryNumber(req.getEntryNumber())
                .dateOfBirth(req.getDateOfBirth())
                .status(StudentStatus.ACTIVE)
                .build());

    }

    @Transactional
    public void updateStudent(UUID uuid, StudentRequestDTO req) {
        Student s = findEntity(uuid);
        s.setFirstName(req.getFirstName());
        s.setLastName(req.getLastName());
        s.setStudentEmail(req.getStudentEmail());
        s.setEntryNumber(req.getEntryNumber());
        s.setDateOfBirth(req.getDateOfBirth());

        studentRepo.save(s);
    }

    @Transactional
    public void deleteStudent(UUID uuid) {
        Student s = findEntity(uuid);
        studentRepo.delete(s);
    }

}