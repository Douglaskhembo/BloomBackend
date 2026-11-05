package com.bloom.bloomschool.student.service;

import com.bloom.bloomschool.document.service.ProvidedDocumentService;
import com.bloom.bloomschool.feestructure.service.FeePaymentService;
import com.bloom.bloomschool.gradeLevel.dto.GradeSummaryDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.gradeLevel.service.GradeService;
import com.bloom.bloomschool.student.entity.Admissions;
import com.bloom.bloomschool.document.entity.DocumentType;
import com.bloom.bloomschool.student.enums.ApplicationStage;
import com.bloom.bloomschool.document.enums.OwnerType;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.student.dto.*;
import com.bloom.bloomschool.student.entity.Student;
import com.bloom.bloomschool.student.enums.StudentStatus;
import com.bloom.bloomschool.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


import com.bloom.bloomschool.gradeLevel.repository.GradeRepository;
import com.bloom.bloomschool.setup.service.ServiceGenerator;
import com.bloom.bloomschool.student.repository.AdmissionsRepository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepo;
    private final GradeRepository gradeRepo;
    private final ServiceGenerator serviceGenerator;
    private final AdmissionsRepository admissionsRepo;
    private final FeePaymentService feePaymentService;
    private final ProvidedDocumentService providedDocumentService;
    private final GradeService gradeService;
    @Transactional
    public void createAdmission(AdmissionRequestDto req){
        GradeEntity grade = gradeRepo.findByUuid(req.getGradeUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found"));
        String applicationId = serviceGenerator.generateReference("APP");
        Admissions admissions = Admissions.builder()
                .applicationId(applicationId)
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .gender(req.getGender())
                .dateOfBirth(req.getDateOfBirth())
                .birthCertificateNumber(req.getBirthCertificateNumber())
                .address(req.getAddress())
                .medicalNote(req.getMedicalNote())
                .stream(req.getStream())
                .applicationStage(ApplicationStage.APPLICATION_REVIEW)
                .studentType(req.getStudentType())
                .parentName(req.getParentName())
                .parentRelationship(req.getParentRelationship())
                .parentPhoneNumber(req.getParentPhoneNumber())
                .parentEmail(req.getParentEmail())
                .gradeEntity(grade).build();
        admissionsRepo.save(admissions);
    }

    @Transactional(readOnly = true)
    public List<AdmissionResponseDto> getAllAdmissions(){
        List<Admissions> admissions = admissionsRepo.findAll();
        return admissions.stream().map(this::convertToDto).toList();
    }

    @Transactional(readOnly = true)
    public AdmissionResponseDto getAdmission(UUID uuid){
        Admissions admissions = admissionsRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Admission record not found"));
        return convertToDto(admissions);
    }
    private AdmissionResponseDto convertToDto(Admissions body){
        GradeSummaryDto gradeDto = null;
        if(body.getGradeEntity() != null){
            GradeEntity gradeEntity = body.getGradeEntity();
            gradeDto = GradeSummaryDto.builder()
                    .uuid(gradeEntity.getUuid())
                    .name(gradeEntity.getName())
                    .build();
        }

        return AdmissionResponseDto.builder()
                .uuid(body.getUuid())
                .applicationId(body.getApplicationId())
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .gender(body.getGender())
                .dateOfBirth(body.getDateOfBirth())
                .birthCertificateNumber(body.getBirthCertificateNumber())
                .address(body.getAddress())
                .medicalNote(body.getMedicalNote())
                .stream(body.getStream())
                .applicationStage(body.getApplicationStage())
                .studentType(body.getStudentType())
                .parentName(body.getParentName())
                .parentRelationship(body.getParentRelationship())
                .parentPhoneNumber(body.getParentPhoneNumber())
                .parentEmail(body.getParentEmail())
                .grade(gradeDto)
                .build();
    }

    @Transactional
    public void updateAdmission(UUID uuid, AdmissionRequestDto req){
        Admissions existingAdmissions = admissionsRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Admission record not found"));

        existingAdmissions.setFirstName(req.getFirstName());
        existingAdmissions.setLastName(req.getLastName());
        existingAdmissions.setGender(req.getGender());
        existingAdmissions.setDateOfBirth(req.getDateOfBirth());
        existingAdmissions.setBirthCertificateNumber(req.getBirthCertificateNumber());
        existingAdmissions.setAddress(req.getAddress());
        existingAdmissions.setMedicalNote(req.getMedicalNote());
        existingAdmissions.setStream(req.getStream());
        existingAdmissions.setStudentType(req.getStudentType());
        existingAdmissions.setParentName(req.getParentName());
        existingAdmissions.setParentRelationship(req.getParentRelationship());
        existingAdmissions.setParentEmail(req.getParentEmail());
        existingAdmissions.setParentPhoneNumber(req.getParentPhoneNumber());

        if (req.getGradeUuid() != null) {
            GradeEntity grade = gradeRepo.findByUuid(req.getGradeUuid())
                    .orElseThrow(() -> new ResourceNotFoundException("Grade not found"));
            existingAdmissions.setGradeEntity(grade);
        }
        admissionsRepo.save(existingAdmissions);
    }

    @Transactional
    public void deleteAdmission(UUID uuid){
        Admissions admissions = admissionsRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Admission record not found"));
        admissionsRepo.delete(admissions);
    }

    @Transactional
    public void updateAdmissionStage(UUID uuid, ApplicationStage stage, LocalDate joinDate){
        Admissions admissions = admissionsRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Admission record not found"));

        if (admissions.getApplicationStage() == ApplicationStage.ENROLLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This applicant is already enrolled");
        }

        if(stage == ApplicationStage.ENROLLED && !feePaymentService.hasConfirmedPayment(uuid)){
            throw new RuntimeException("Cannot enroll "+ admissions.getFirstName() + " "+ admissions.getLastName()
            +" no confirmed fee payment for this admission");
        }

         if (stage == ApplicationStage.ENROLLED){
             List<DocumentType> missing = providedDocumentService.getMissingMandatory(OwnerType.STUDENT, uuid);
            if(!missing.isEmpty()){
                String names = missing.stream().map(DocumentType::getName).reduce((x, y) -> x + ", "+y)
                        .orElse("");
                throw  new RuntimeException("Cannot enroll "+ admissions.getFirstName()+ " "+admissions.getLastName()+ " mandatory documents not provided: "+names);
            }
        }

        admissions.setApplicationStage(stage);
        admissionsRepo.save(admissions);

        if(stage == ApplicationStage.ENROLLED) createStudentOnAdmission(admissions, joinDate);
    }

    private void createStudentOnAdmission(Admissions body, LocalDate joinDate){
        gradeService.isStreamFull(body.getGradeEntity(), body.getStream());

        Student saved = studentRepo.save(Student.builder()
                .admissionNumber(serviceGenerator.generateYearlyReference("ADM"))
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .dateOfBirth(body.getDateOfBirth())
                .entryNumber(serviceGenerator.generateYearlyReference("ENT"))
                .studentEmail(null)
                .status(StudentStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .joinDate(joinDate != null ? joinDate : LocalDate.now())
                .gradeEntity(body.getGradeEntity())
                .stream(body.getStream())
                .build());

        body.setStudent(saved);
        admissionsRepo.save(body);
    }


    private Student findEntity(UUID uuid) {
        return studentRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    private StudentResponseDTO toResponse(Student s) {
        return StudentResponseDTO.builder()
                .uuid(s.getUuid())
                .admissionNumber(s.getAdmissionNumber())
                .fullName(s.getFirstName() + " " + s.getLastName())
                .studentEmail(s.getStudentEmail())
                .entryNumber(s.getEntryNumber())
                .dateOfBirth(s.getDateOfBirth())
                .status(s.getStatus() != null ? s.getStatus().name() : "N/A")
                .grade(s.getGradeEntity() != null ? s.getGradeEntity().getName() : null)
                .stream(s.getStream())
                .build();
    }

    public List<StudentResponseDTO> getAllStudents() {
        return studentRepo.findAll().stream().map(this::toResponse).toList();
    }

    public StudentResponseDTO getStudentByUuid(UUID uuid) {
        return toResponse(findEntity(uuid));
    }

    @Transactional
    public void updateStudent(UUID uuid, StudentRequestDTO req) {
        Student s = findEntity(uuid);

        s.setFirstName(req.getFirstName());
        s.setLastName(req.getLastName());
        s.setStudentEmail(req.getStudentEmail());
        s.setDateOfBirth(req.getDateOfBirth());

        studentRepo.save(s);
    }

    @Transactional
    public void deleteStudent(UUID uuid) {
        Student s = findEntity(uuid);
        studentRepo.delete(s);
    }

}