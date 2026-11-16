package com.bloom.bloomschool.feestructure.service;

import com.bloom.bloomschool.feestructure.dto.*;
import com.bloom.bloomschool.feestructure.entity.FeeItemEntity;
import com.bloom.bloomschool.feestructure.entity.FeeStructureEntity;
import com.bloom.bloomschool.feestructure.entity.FeeStructureLine;
import com.bloom.bloomschool.feestructure.enumerations.FeeStructureStatus;
import com.bloom.bloomschool.feestructure.repository.FeeItemRepository;
import com.bloom.bloomschool.feestructure.repository.FeeStructureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeStructureService {
    private final FeeStructureRepository repo;
    private final FeeItemRepository feeItemRepo;
    public List<FeeStructureResponseDto> getAllFeeStructures(){
        return repo.findAll().stream().map(this::convertToDto).toList();
    }

    public List<FeeStructureResponseDto> getAllFeeStructuresDescOrder(){
        return repo.findAll(Sort.by(Sort.Direction.DESC, "submittedAt")).stream().map(this::convertToDto).toList();
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
    public FeeStructureEntity submitForApproval(FeeStructureRequestDto body){
        if (body.getReworkUuid() != null) {
            FeeStructureEntity feeStructure = repo.findByUuid(body.getReworkUuid())
                    .orElseThrow(() -> new EntityNotFoundException("Fee structure not found"));
            if (feeStructure.getStatus() != FeeStructureStatus.REJECTED) {
                throw new IllegalArgumentException("Only rejected fee structure can be reworked");
            }
            feeStructure.setFeeStructureLines(new ArrayList<>(convertLine(body.getFeeStructureLines())));
            feeStructure.setBaseline(new ArrayList<>(convertBaseLine(body.getGrade(), body.getTerm())));
            feeStructure.setComment(body.getComment());
            feeStructure.setRejectReason(null);
            feeStructure.setStatus(FeeStructureStatus.PENDING);
            return repo.save(feeStructure);
        }

        if (repo.existsByAcademicYearAndGradeAndTerm(
                body.getAcademicYear(), body.getGrade(), body.getTerm())) {
            throw new RuntimeException(
                    "A fee structure for this academic year, grade and term already exists");
        }

        return repo.save(FeeStructureEntity.builder()
                .academicYear(body.getAcademicYear())
                .grade(body.getGrade())
                .term(body.getTerm())
                .status(FeeStructureStatus.PENDING)
                .comment(body.getComment())
                .feeStructureLines(new ArrayList<>(convertLine(body.getFeeStructureLines())))
                .baseline(new ArrayList<>(convertBaseLine(body.getGrade(), body.getTerm())))
                .build());
    }

    private List<FeeStructureLine> convertLine(List<FeeStructureLineRequestDto> body){
        return body.stream().map(this::convertFeeStructureLineToEntity).toList();
    }

    private List<FeeStructureLine> convertBaseLine(String grade, String term){
        return mergeWithCurrentItems(
                repo.findFirstByGradeAndTermStatusOrderByReviewedAtDesc(grade, term, FeeStructureStatus.APPROVED), term
        );

    }

    private List<FeeStructureLine> mergeWithCurrentItems(List<FeeStructureLine> existing, String term) {
        List<FeeItemEntity> allItems = feeItemRepo.findAll();
        Set<UUID> catalogIds = allItems.stream().map(FeeItemEntity::getUuid).collect(Collectors.toSet());
        Set<UUID> knownIds = existing.stream().map(FeeStructureLine::getItemId).collect(Collectors.toSet());

        List<FeeStructureLine> merged = new ArrayList<>();
        for (FeeStructureLine line : existing) {
            if (catalogIds.contains(line.getItemId())) merged.add(line);
        }
        for (FeeItemEntity item : allItems) {
            if (!knownIds.contains(item.getUuid())) {
                merged.add(FeeStructureLine.builder()
                        .itemId(item.getUuid())
                        .enabled(false)
                        .amount(resolveAmount(item, term))
                        .build());
            }
        }
        return merged;
    }

    private double resolveAmount(FeeItemEntity item, String structureTerm) {
        if ("Per Term".equals(item.getTerm())) {
            Double perTerm = switch (structureTerm) {
                case "Term 1" -> item.getTermOneAmount();
                case "Term 2" -> item.getTermTwoAmount();
                case "Term 3" -> item.getTermThreeAmount();
                default -> null;
            };
            if (perTerm != null) return perTerm;
        }
        return item.getAmount() != null ? item.getAmount() : 0;
    }

    private FeeStructureLine convertFeeStructureLineToEntity(FeeStructureLineRequestDto body){
        return FeeStructureLine.builder()
                .itemId(body.getItemId())
                .enabled(body.isEnabled())
                .amount(body.getAmount())
                .build();
    }

    @Transactional
    public void deleteFeeStructure(UUID uuid){
        FeeStructureEntity existingFeeStructure = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee Structure not found"));
        repo.delete(existingFeeStructure);
    }

    @Transactional
    public FeeStructureEntity saveDraft(FeeStructureRequestDto body){
        if (repo.existsByAcademicYearAndGradeAndTerm(
                body.getAcademicYear(), body.getGrade(), body.getTerm())) {
            throw new RuntimeException(
                    "A fee structure for this academic year, grade and term already exists");
        }

        List<FeeStructureLine> lines = body.getFeeStructureLines()
                .stream().map(this::convertFeeStructureLineToEntity).toList();

        return repo.save(FeeStructureEntity.builder()
                .academicYear(body.getAcademicYear())
                .grade(body.getGrade())
                .term(body.getTerm())
                .status(FeeStructureStatus.DRAFT)
                .comment(body.getComment())
                .feeStructureLines(new ArrayList<>(lines))
                .baseline(new ArrayList<>(lines))
                .submittedAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public void approveFeeStructure(UUID feeStructureUuid){
        FeeStructureEntity existingFeeStructure = repo.findByUuid(feeStructureUuid)
                .orElseThrow(() -> new EntityNotFoundException("Fee Structure not found"));

        if(existingFeeStructure.getStatus() != FeeStructureStatus.PENDING && existingFeeStructure.getStatus() != FeeStructureStatus.DRAFT){
            throw  new RuntimeException("Fee structure status already processed");
        }

        existingFeeStructure.setStatus(FeeStructureStatus.APPROVED);
        existingFeeStructure.setUpdatedAt(LocalDateTime.now());
        existingFeeStructure.setReviewedAt(LocalDateTime.now());
        repo.save(existingFeeStructure);
    }

    @Transactional
    public void rejectFeeStructure(UUID feeStructureUuid, FeeStructureDecisionRequest decisionRequest){
        FeeStructureEntity existingFeeStructure = repo.findByUuid(feeStructureUuid)
                .orElseThrow(() -> new EntityNotFoundException("Fee Structure not found"));

        if(existingFeeStructure.getStatus() != FeeStructureStatus.PENDING && existingFeeStructure.getStatus() != FeeStructureStatus.DRAFT){
            throw  new RuntimeException("Fee structure status already processed");
        }

        existingFeeStructure.setStatus(FeeStructureStatus.REJECTED);
        existingFeeStructure.setRejectReason(decisionRequest.getReason());
        existingFeeStructure.setUpdatedAt(LocalDateTime.now());
        existingFeeStructure.setReviewedAt(LocalDateTime.now());
        repo.save(existingFeeStructure);
    }

}
