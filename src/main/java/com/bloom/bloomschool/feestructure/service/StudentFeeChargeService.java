package com.bloom.bloomschool.feestructure.service;

import com.bloom.bloomschool.feestructure.dto.StudentFeeChargeRequestDto;
import com.bloom.bloomschool.feestructure.dto.StudentFeeChargeResponseDto;
import com.bloom.bloomschool.feestructure.entity.FeeItemEntity;
import com.bloom.bloomschool.feestructure.entity.FeeStructureEntity;
import com.bloom.bloomschool.feestructure.entity.StudentFeeCharge;
import com.bloom.bloomschool.feestructure.repository.FeeItemRepository;
import com.bloom.bloomschool.feestructure.repository.FeeStructureRepository;
import com.bloom.bloomschool.feestructure.repository.StudentFeeStructureRepository;
import com.bloom.bloomschool.student.dto.StudentSummaryDto;
import com.bloom.bloomschool.student.entity.Student;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentFeeChargeService {
    private final StudentFeeStructureRepository repo;
    private final StudentRepository studentRepository;
    private final FeeItemRepository feeItemRepository;
    private final FeeStructureRepository feeStructureRepository;

    public List<StudentFeeChargeResponseDto> getAll(){
        return repo.findAll().stream().map(this::convertToDto).toList();

    }
    private StudentFeeChargeResponseDto convertToDto(StudentFeeCharge body){
        Student student = body.getStudent();
        StudentSummaryDto studentRequestDTO = StudentSummaryDto.builder()
                .admissionNumber(student.getAdmissionNumber())
                .fullName(student.getFirstName()+" "+student.getLastName())
                .build();

        return StudentFeeChargeResponseDto.builder()
                .uuid(body.getUuid())
                .admissionNumber(body.getAdmissionNumber())
                .grade(body.getGrade())
                .stream(body.getStream())
                .structureVersion(body.getStructureVersion())
                .academicYear(body.getAcademicYear())
                .period(body.getPeriod())
                .itemName(body.getItemName())
                .feeCategory(body.getFeeCategory())
                .amount(body.getAmount())
                .dueDate(body.getDueDate())
                .generatedAt(body.getGeneratedAt())
                .feeItemUuid(body.getFeeItemEntities().getUuid())
                .student(studentRequestDTO)
                .feeStructureUuid(body.getFeeStructureEntity().getUuid())
                .build();
    }

    public StudentFeeChargeResponseDto getFeeCharge(UUID uuid) {
        StudentFeeCharge studentFeeCharge = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No Student fee charge found"));
        return convertToDto(studentFeeCharge);
    }

    @Transactional
    public void create(StudentFeeChargeRequestDto req){
        FeeItemEntity existingFeeItem = feeItemRepository.findByUuid(req.getFeeItemUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Fee item found"));
        FeeStructureEntity existingFeeStructure = feeStructureRepository.findByUuid(req.getFeeStructureUuid())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No Fee structure found"));
        Student student = studentRepository.findByUuid(req.getStudentUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        StudentFeeCharge studentFeeCharge = StudentFeeCharge.builder()
                .admissionNumber(req.getAdmissionNumber())
                .grade(req.getGrade())
                .stream(req.getStream())
                .structureVersion(req.getStructureVersion())
                .academicYear(req.getAcademicYear())
                .period(req.getPeriod())
                .itemName(req.getItemName())
                .feeCategory(req.getFeeCategory())
                .amount(req.getAmount())
                .dueDate(req.getDueDate())
                .generatedAt(req.getGeneratedAt())
                .feeStructureEntity(existingFeeStructure)
                .student(student)
                .feeItemEntities(existingFeeItem).build();
        repo.save(studentFeeCharge);
    }

    @Transactional
    public void update(UUID uuid, StudentFeeChargeRequestDto req) {
        FeeItemEntity existingFeeItem = feeItemRepository.findByUuid(req.getFeeItemUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Fee item found"));
        FeeStructureEntity existingFeeStructure = feeStructureRepository.findByUuid(req.getFeeStructureUuid())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No Fee structure found"));
        Student student = studentRepository.findByUuid(req.getStudentUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        StudentFeeCharge existingFeeCharge = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"No fee charge found"));

        existingFeeCharge.setAdmissionNumber(req.getAdmissionNumber());
        existingFeeCharge.setGrade(req.getGrade());
        existingFeeCharge.setStream(req.getStream());
        existingFeeCharge.setStructureVersion(req.getStructureVersion());
        existingFeeCharge.setAcademicYear(req.getAcademicYear());
        existingFeeCharge.setPeriod(req.getPeriod());
        existingFeeCharge.setItemName(req.getItemName());
        existingFeeCharge.setFeeCategory(req.getFeeCategory());
        existingFeeCharge.setAmount(req.getAmount());
        existingFeeCharge.setDueDate(req.getDueDate());
        existingFeeCharge.setGeneratedAt(req.getGeneratedAt());
        existingFeeCharge.setFeeStructureEntity(existingFeeStructure);
        existingFeeCharge.setStudent(student);
        existingFeeCharge.setFeeItemEntities(existingFeeItem);
        repo.save(existingFeeCharge);
    }

    @Transactional
    public void delete(UUID uuid){
        StudentFeeCharge existingFeeCharge = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee charge structure not found"));
        repo.delete(existingFeeCharge);
    }
}
