package com.bloom.bloomschool.feestructure.service;

import com.bloom.bloomschool.feestructure.dto.FeeItemDto;
import com.bloom.bloomschool.feestructure.entity.FeeItemEntity;
import com.bloom.bloomschool.feestructure.repository.FeeItemRepository;
import com.bloom.bloomschool.gradeLevel.dto.GradeDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.gradeLevel.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FeeItemService {
    private final FeeItemRepository repo;
    private final GradeRepository gradeRepo;

    public List<FeeItemDto> getAll(){
        return repo.findAll().stream().map(this::convertToDto).toList();
    }

    public FeeItemDto getFeeItem(UUID uuid){
        return convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No fee item found")));
    }
    private FeeItemDto convertToDto(FeeItemEntity body) {
        Set<GradeDto> gradeDtos = new HashSet<>();
        if (body.getGradeLevel() != null) {
            gradeDtos = body.getGradeLevel().stream()
                    .map(gradeEntity -> GradeDto.builder()
                            .uuid(gradeEntity.getUuid())
                            .name(gradeEntity.getName())
                            .displayOrder(gradeEntity.getDisplayOrder())
                            .capacity(gradeEntity.getCapacity())
                            .isActive(gradeEntity.getIsActive())
                            .streamName(gradeEntity.getStreamName())
                            .streamCapacity(gradeEntity.getStreamCapacity())
                            .streams(gradeEntity.getStreams())
                            .build())
                    .collect(Collectors.toSet());
        }

        return FeeItemDto.builder()
                .uuid(body.getUuid())
                .name(body.getName())
                .description(body.getDescription())
                .term(body.getTerm())
                .amount(body.getAmount())
                .termOneAmount(body.getTermOneAmount())
                .termTwoAmount(body.getTermTwoAmount())
                .termThreeAmount(body.getTermThreeAmount())
                .gradeLevel(gradeDtos)
                .mandatory(body.getMandatory())
                .active(body.isActive())
                .feeCategory(body.getFeeCategory())
                .feeApplicability(body.getFeeApplicability())
                .build();
    }

    @Transactional
    public void createFeeItem(FeeItemDto body){
        Set<GradeEntity> gradeEntity =new HashSet<>(gradeRepo.findAllByUuidIn(body.getGradeUuids()));

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
                .active(body.isActive()).feeCategory(body.getFeeCategory())
                .feeApplicability(body.getFeeApplicability())
                .mandatory(body.getMandatory())
                .build();
        repo.save(feeItemEntity);
    }

    @Transactional
    public void updateFeeItem(FeeItemDto body, UUID uuid) {
        FeeItemEntity existingFeeItem = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Fee item found"));

        if (repo.existsByNameAndUuidNot(body.getName(), uuid)) {
            throw new RuntimeException("A fee item with this name already exists");
        }

        Set<GradeEntity> gradeEntity =new HashSet<>(gradeRepo.findAllByUuidIn(body.getGradeUuids()));

        existingFeeItem.setName(body.getName());
        existingFeeItem.setDescription(body.getDescription());
        existingFeeItem.setTerm(body.getTerm());
        existingFeeItem.setAmount(body.getAmount());
        existingFeeItem.setTermOneAmount(body.getTermOneAmount());
        existingFeeItem.setTermTwoAmount(body.getTermTwoAmount());
        existingFeeItem.setTermThreeAmount(body.getTermThreeAmount());
        existingFeeItem.setGradeLevel(gradeEntity);
        existingFeeItem.setMandatory(body.getMandatory());
        existingFeeItem.setActive(body.isActive());
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
