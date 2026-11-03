package com.bloom.bloomschool.feestructure.service;

import com.bloom.bloomschool.feestructure.dto.FeeStructureLineRequestDto;
import com.bloom.bloomschool.feestructure.dto.FeeStructureLineResponseDto;
import com.bloom.bloomschool.feestructure.dto.FeeStructureRequestDto;
import com.bloom.bloomschool.feestructure.dto.FeeStructureResponseDto;
import com.bloom.bloomschool.feestructure.entity.FeeStructureEntity;
import com.bloom.bloomschool.feestructure.entity.FeeStructureLine;
import com.bloom.bloomschool.feestructure.repository.FeeStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeeStructureService {
    private final FeeStructureRepository repo;

    public List<FeeStructureResponseDto> getAll(){
        return repo.findAll().stream().map(this::convertToDto).toList();
    }

    public FeeStructureResponseDto getFeeStructure(UUID uuid) {
        return convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No Fee structure found")));
    }

    private FeeStructureLineResponseDto convertFeeStructureLinesToDto(FeeStructureLine body){
        return FeeStructureLineResponseDto.builder()
                .itemId(body.getItemId())
                .enabled(body.isEnabled())
                .amount(body.getAmount()).build();
    }
    private FeeStructureResponseDto convertToDto(FeeStructureEntity body){
        List<FeeStructureLineResponseDto> feeStructureLineResponseDtos = body.getFeeStructureLines()
                .stream()
                .map(this::convertFeeStructureLinesToDto).toList();
        return FeeStructureResponseDto.builder()
                .uuid(body.getUuid())
                .academicYear(body.getAcademicYear())
                .grade(body.getGrade())
                .term(body.getTerm())
                .status(body.getStatus())
                .version(body.getVersion())
                .comment(body.getComment())
                .rejectReason(body.getRejectReason())
                .feeStructureLines(feeStructureLineResponseDtos)
                .baseline(feeStructureLineResponseDtos)
                .build();
    }

    @Transactional
    public void createFeeStructure(FeeStructureRequestDto body){
        if (repo.existsByAcademicYearAndGradeAndTerm(
                body.getAcademicYear(), body.getGrade(), body.getTerm())) {
            throw new RuntimeException(
                    "A fee structure for this academic year, grade and term already exists");
        }
        repo.save(convertTOEntity(body));
    }

    @Transactional
    public void updateFeeStructure(UUID uuid, FeeStructureRequestDto body) {
        FeeStructureEntity existingFeeStructure = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No Fee structure found"));

        if (repo.existsByAcademicYearAndGradeAndTermAndUuidNot(
                body.getAcademicYear(), body.getGrade(), body.getTerm(), uuid)) {
            throw new RuntimeException(
                    "A fee structure for this academic year, grade and term already exists");
        }

        List<FeeStructureLine> feeStructureLines = body.getFeeStructureLines()
                .stream().map(this::convertFeeStructureLineToEntity).toList();

        existingFeeStructure.setAcademicYear(body.getAcademicYear());
        existingFeeStructure.setGrade(body.getGrade());
        existingFeeStructure.setTerm(body.getTerm());
        existingFeeStructure.setStatus(body.getStatus());
        existingFeeStructure.setVersion(body.getVersion());
        existingFeeStructure.setComment(body.getComment());
        existingFeeStructure.setRejectReason(body.getRejectReason());
        existingFeeStructure.setFeeStructureLines(feeStructureLines);
        existingFeeStructure.setBaseline(feeStructureLines);

        repo.save(existingFeeStructure);
    }

    private FeeStructureLine convertFeeStructureLineToEntity(FeeStructureLineRequestDto body){
        return FeeStructureLine.builder()
                .itemId(body.getItemId())
                .enabled(body.isEnabled())
                .amount(body.getAmount())
                .build();
    }
    private FeeStructureEntity convertTOEntity(FeeStructureRequestDto body){
        List<FeeStructureLine> feeStructureLines = body.getFeeStructureLines()
                .stream().map(this::convertFeeStructureLineToEntity).toList();
        return FeeStructureEntity.builder()
                .academicYear(body.getAcademicYear())
                .grade(body.getGrade())
                .term(body.getTerm())
                .status(body.getStatus())
                .version(body.getVersion())
                .comment(body.getComment())
                .rejectReason(body.getRejectReason())
                .feeStructureLines(feeStructureLines)
                .baseline(feeStructureLines)
                .build();
    }

    @Transactional
    public void deleteFeeStructure(UUID uuid){
        FeeStructureEntity existingFeeAssessment = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee Structure not found"));
        repo.delete(existingFeeAssessment);
    }
}
