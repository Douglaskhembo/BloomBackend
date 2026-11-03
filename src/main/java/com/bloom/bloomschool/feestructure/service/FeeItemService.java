package com.bloom.bloomschool.feestructure.service;

import com.bloom.bloomschool.feestructure.dto.FeeItemRequestDto;
import com.bloom.bloomschool.feestructure.dto.FeeItemResponseDto;
import com.bloom.bloomschool.feestructure.entity.FeeItemEntity;
import com.bloom.bloomschool.feestructure.repository.FeeItemRepository;
import com.bloom.bloomschool.gradeLevel.dto.GradeSummaryDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.gradeLevel.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FeeItemService {
    private final FeeItemRepository repo;
    private final GradeRepository gradeRepo;

    public List<FeeItemResponseDto> getAll(){
        return repo.findAll().stream().map(this::convertToDto).toList();
    }

    public FeeItemResponseDto getFeeItem(UUID uuid){
        return convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No fee item found")));
    }
    private FeeItemResponseDto convertToDto(FeeItemEntity body) {
        List<GradeSummaryDto> gradeDtos = new ArrayList<>();
        if (body.getGradeLevel() != null) {
            gradeDtos = body.getGradeLevel().stream()
                    .map(gradeEntity -> GradeSummaryDto.builder()
                            .uuid(gradeEntity.getUuid())
                            .name(gradeEntity.getName())
                            .build())
                    .collect(Collectors.toList());
        }

        return FeeItemResponseDto.builder()
                .uuid(body.getUuid())
                .name(body.getName())
                .description(body.getDescription())
                .term(body.getTerm())
                .amount(body.getAmount())
                .termOneAmount(body.getTermOneAmount())
                .termTwoAmount(body.getTermTwoAmount())
                .termThreeAmount(body.getTermThreeAmount())
                .grades(gradeDtos)
                .mandatory(body.getMandatory())
                .active(body.isActive())
                .feeCategory(body.getFeeCategory())
                .feeApplicability(body.getFeeApplicability())
                .build();
    }

    private Set<GradeEntity> resolveGrades(List<UUID> uuids){
        if (uuids == null || uuids.isEmpty()) return new HashSet<>();

        List<UUID> distinct = uuids.stream().distinct().toList();
        List<GradeEntity> found = gradeRepo.findAllByUuidIn(distinct);

        if (found.size() != distinct.size()) {
            Set<UUID> foundIds = found.stream().map(GradeEntity::getUuid).collect(Collectors.toSet());
            List<UUID> missing = distinct.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade(s) not found: " + missing);
        }
        return new HashSet<>(found);
    }
    @Transactional
    public void createFeeItem(FeeItemRequestDto body){
        Set<GradeEntity> gradeEntity =resolveGrades(body.getGradeUuids());

        if (repo.existsByName(body.getName())) {
            throw new RuntimeException("A fee item with this name already exists");
        }
        FeeItemEntity feeItemEntity = FeeItemEntity.builder()
                .name(body.getName())
                .description(body.getDescription())
                .term(body.getTerm())
                .amount(body.getAmount())
                .termOneAmount(body.getTermOneAmount())
                .termTwoAmount(body.getTermTwoAmount())
                .termThreeAmount(body.getTermThreeAmount())
                .gradeLevel(gradeEntity).mandatory(body.getMandatory())
                .active(body.getActive()).feeCategory(body.getFeeCategory())
                .feeApplicability(body.getFeeApplicability())
                .mandatory(body.getMandatory())
                .build();
        repo.save(feeItemEntity);
    }

    @Transactional
    public void updateFeeItem(FeeItemRequestDto body, UUID uuid) {
        FeeItemEntity existingFeeItem = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Fee item found"));

        if (repo.existsByNameAndUuidNot(body.getName(), uuid)) {
            throw new RuntimeException("A fee item with this name already exists");
        }

        Set<GradeEntity> gradeEntity =resolveGrades(body.getGradeUuids());

        existingFeeItem.setName(body.getName());
        existingFeeItem.setDescription(body.getDescription());
        existingFeeItem.setTerm(body.getTerm());
        existingFeeItem.setAmount(body.getAmount());
        existingFeeItem.setTermOneAmount(body.getTermOneAmount());
        existingFeeItem.setTermTwoAmount(body.getTermTwoAmount());
        existingFeeItem.setTermThreeAmount(body.getTermThreeAmount());
        existingFeeItem.setGradeLevel(gradeEntity);
        existingFeeItem.setMandatory(body.getMandatory());
        existingFeeItem.setActive(body.getActive());
        existingFeeItem.setFeeCategory(body.getFeeCategory());
        existingFeeItem.setFeeApplicability(body.getFeeApplicability());
        repo.save(existingFeeItem);
    }

    @Transactional
    public void toggleFeeItemStatus(UUID uuid) {
        FeeItemEntity existingFeeItem = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Fee item found"));
        existingFeeItem.setActive(!existingFeeItem.isActive());
        repo.save(existingFeeItem);
    }

    @Transactional
    public void deleteFeeItem(UUID uuid){
        FeeItemEntity existingFeeItem = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee Item not found"));
        repo.delete(existingFeeItem);
    }
}
