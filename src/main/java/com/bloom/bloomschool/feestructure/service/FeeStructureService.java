package com.bloom.bloomschool.feestructure.service;

import com.bloom.bloomschool.assessment.repository.AssessmentRepository;
import com.bloom.bloomschool.feestructure.dto.FeeStructureDto;
import com.bloom.bloomschool.feestructure.entity.FeeStructureEntity;
import com.bloom.bloomschool.feestructure.repository.FeeStructureRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeeStructureService {
    private final FeeStructureRepository repo;
    public List<FeeStructureDto> getAll(){
        return repo.findAll().stream().map(this::convertToDto).toList();
    }

    public FeeStructureDto getFeeStructure(UUID uuid) {
        return convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No Fee structure found")));
    }

    private FeeStructureDto convertToDto(FeeStructureEntity body){
        return FeeStructureDto.builder()
                .uuid(body.getUuid())
                .academicYear(body.getAcademicYear())
                .grade(body.getGrade())
                .term(body.getTerm())
                .status(body.getStatus())
                .version(body.getVersion())
                .comment(body.getComment())
                .rejectReason(body.getRejectReason())
                .feeStructureLines(body.getFeeStructureLines())
                .baseline(body.getBaseline())
                .build();
    }

    @Transactional
    public void createFeeStructure(FeeStructureDto body){
        if (repo.existsByAcademicYearAndGradeAndTerm(
                body.getAcademicYear(), body.getGrade(), body.getTerm())) {
            throw new RuntimeException(
                    "A fee structure for this academic year, grade and term already exists");
        }
        repo.save(convertTOEntity(body));
    }

    @Transactional
    public void updateFeeStructure(UUID uuid, FeeStructureDto body) {
        FeeStructureEntity existingFeeStructure = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No Fee structure found"));

        if (repo.existsByAcademicYearAndGradeAndTermAndUuidNot(
                body.getAcademicYear(), body.getGrade(), body.getTerm(), uuid)) {
            throw new RuntimeException(
                    "A fee structure for this academic year, grade and term already exists");
        }

        existingFeeStructure.setAcademicYear(body.getAcademicYear());
        existingFeeStructure.setGrade(body.getGrade());
        existingFeeStructure.setTerm(body.getTerm());
        existingFeeStructure.setStatus(body.getStatus());
        existingFeeStructure.setVersion(body.getVersion());
        existingFeeStructure.setComment(body.getComment());
        existingFeeStructure.setRejectReason(body.getRejectReason());
        existingFeeStructure.setFeeStructureLines(body.getFeeStructureLines());
        existingFeeStructure.setBaseline(body.getBaseline());

        repo.save(existingFeeStructure);
    }
    private FeeStructureEntity convertTOEntity(FeeStructureDto body){
        return FeeStructureEntity.builder()
                .academicYear(body.getAcademicYear())
                .grade(body.getGrade())
                .term(body.getTerm())
                .status(body.getStatus())
                .version(body.getVersion())
                .comment(body.getComment())
                .rejectReason(body.getRejectReason())
                .feeStructureLines(body.getFeeStructureLines())
                .baseline(body.getFeeStructureLines())
                .build();
    }

    @Transactional
    public void deleteFeeStructure(UUID uuid){
        FeeStructureEntity existingFeeAssessment = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee Structure not found"));
        repo.delete(existingFeeAssessment);
    }
}
