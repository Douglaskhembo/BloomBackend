package com.bloom.bloomschool.feestructure.service;

import com.bloom.bloomschool.feestructure.dto.FeeItemDto;
import com.bloom.bloomschool.feestructure.dto.FeeStructureDto;
import com.bloom.bloomschool.feestructure.dto.StudentFeeChargeDto;
import com.bloom.bloomschool.feestructure.entity.FeeItemEntity;
import com.bloom.bloomschool.feestructure.entity.FeeStructureEntity;
import com.bloom.bloomschool.feestructure.entity.StudentFeeCharge;
import com.bloom.bloomschool.feestructure.repository.StudentFeeStructureRepository;
import com.bloom.bloomschool.student.dto.StudentRequestDTO;
import com.bloom.bloomschool.student.entity.Student;
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

    public List<StudentFeeChargeDto> getAll(){
        return repo.findAll().stream().map(this::convertToDto).toList();

    }
    private StudentFeeChargeDto convertToDto(StudentFeeCharge body){
        Student student = body.getStudent();
        StudentRequestDTO studentRequestDTO = StudentRequestDTO.builder()
                .admissionNumber(student.getAdmissionNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .studentEmail(student.getStudentEmail())
                .entryNumber(student.getEntryNumber())
                .dateOfBirth(student.getDateOfBirth())
                .build();

        FeeStructureEntity feeStructureEntity = body.getFeeStructureEntity();
        FeeStructureDto feeStructureDto = FeeStructureDto.builder()
                .academicYear(feeStructureEntity.getAcademicYear())
                .grade(feeStructureEntity.getGrade())
                .term(feeStructureEntity.getTerm())
                .status(feeStructureEntity.getStatus())
                .version(feeStructureEntity.getVersion())
                .comment(feeStructureEntity.getComment())
                .rejectReason(feeStructureEntity.getRejectReason())
                .feeStructureLines(feeStructureEntity.getFeeStructureLines())
                .baseline(feeStructureEntity.getBaseline())
                .build();

        FeeItemEntity feeItemEntity = body.getFeeItemEntities();
        FeeItemDto feeItemDto = FeeItemDto.builder()
                .name(feeItemEntity.getName())
                .description(feeItemEntity.getDescription())
                .term(feeItemEntity.getTerm())
                .amount(feeItemEntity.getAmount())
                .termOneAmount(feeItemEntity.getTermOneAmount())
                .termTwoAmount(feeItemEntity.getTermTwoAmount())
                .termThreeAmount(feeItemEntity.getTermThreeAmount())
                .mandatory(feeItemEntity.getMandatory())
                .active(feeItemEntity.isActive())
                .feeCategory(feeItemEntity.getFeeCategory())
                .feeApplicability(feeItemEntity.getFeeApplicability())
                .build();

        return StudentFeeChargeDto.builder()
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
                .feeStructureDto(feeStructureDto)
                .studentRequestDTO(studentRequestDTO)
                .feeItemDto(feeItemDto)
                .build();
    }

    public StudentFeeChargeDto getFeeCharge(UUID uuid) {
        StudentFeeCharge studentFeeCharge = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No Student fee charge found"));
        return convertToDto(studentFeeCharge);
    }

    @Transactional
    public void create(StudentFeeChargeDto req){
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
                .feeStructureEntity(req.getFeeStructureEntity())
                .student(req.getStudent())
                .feeItemEntities(req.getFeeItemEntities()).build();
        repo.save(studentFeeCharge);
    }

    @Transactional
    public void update(UUID uuid, StudentFeeChargeDto req) {
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
        existingFeeCharge.setFeeStructureEntity(req.getFeeStructureEntity());
        existingFeeCharge.setStudent(req.getStudent());
        existingFeeCharge.setFeeItemEntities(req.getFeeItemEntities());
        repo.save(existingFeeCharge);
    }

    @Transactional
    public void delete(UUID uuid){
        StudentFeeCharge existingFeeCharge = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee charge structure not found"));
        repo.delete(existingFeeCharge);
    }
}
