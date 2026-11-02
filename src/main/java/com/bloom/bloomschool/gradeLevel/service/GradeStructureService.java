package com.bloom.bloomschool.gradeLevel.service;


import com.bloom.bloomschool.gradeLevel.dto.GradeEntryDto;
import com.bloom.bloomschool.gradeLevel.dto.GradeStructureDto;
import com.bloom.bloomschool.gradeLevel.entity.GradingEntry;
import com.bloom.bloomschool.gradeLevel.entity.GradingStructure;
import com.bloom.bloomschool.gradeLevel.repository.GradeStructureRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeStructureService {
    private final GradeStructureRepository repo;

    @Transactional(readOnly = true)
    public List<GradeStructureDto> get_all(){
        List<GradingStructure> gradingStructures = repo.findAll();
        if (CollectionUtils.isEmpty(gradingStructures)){
            return List.of();
        }
        return gradingStructures.stream().map(this::convertToDto).toList();
    }

    @Transactional(readOnly = true)
    public GradeStructureDto get_structure(UUID uuid){
        return convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No subject structure found")));
    }


    private GradeStructureDto convertToDto(GradingStructure body){
        List<GradeEntryDto> gradeEntryDtos =body.getGradingEntries().stream()
                .map(dto -> GradeEntryDto.builder()
                        .label(dto.getLabel())
                        .maxScore(dto.getMaxScore())
                        .minScore(dto.getMinScore())
                        .points(dto.getPoints())
                        .remarks(dto.getRemarks())
                        .build())
                .toList();

        return GradeStructureDto.builder()
                .uuid(body.getUuid())
                .grade(body.getGrade())
                .gradingEntries(gradeEntryDtos)
                .build();
    }

    private GradingEntry convertToGradeEntity(GradeEntryDto body){

        return GradingEntry.builder()
                .label(body.getLabel())
                .minScore(body.getMinScore())
                .maxScore(body.getMaxScore())
                .points(body.getPoints())
                .remarks(body.getRemarks())
                .build();
    }

    @Transactional
    public void createGradeStructure(GradeStructureDto req){
        if(repo.existsByGrade(req.getGrade())){
            throw new RuntimeException("A grade structure with this grade already exists");
        }

        if (req.getGradingEntries() == null || req.getGradingEntries().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grading entries are required");
        }
        List<GradingEntry> entries = req.getGradingEntries().stream()
                .map(this::convertToGradeEntity)
                .collect(Collectors.toCollection(ArrayList::new));

        repo.save(GradingStructure.builder()
                .grade(req.getGrade())
                .gradingEntries(entries)
                .build());
    }

    @Transactional
    public void updateGradeStructure(UUID uuid, GradeStructureDto req){
        GradingStructure existingGradeStructure = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No grade structure with this uuid found"));
        if (req.getGradingEntries() == null || req.getGradingEntries().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grading entries are required");
        }
        List<GradingEntry> entries = req.getGradingEntries().stream()
                .map(this::convertToGradeEntity)
                .collect(Collectors.toCollection(ArrayList::new));

        existingGradeStructure.setGrade(req.getGrade());
        existingGradeStructure.getGradingEntries().clear();
        existingGradeStructure.getGradingEntries().addAll(entries);

        repo.save(existingGradeStructure);
    }

    @Transactional
    public void deleteGradeStructure(UUID uuid){
        GradingStructure existingGradeStructure = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No grade structure with this uuid found"));
        repo.delete(existingGradeStructure);
    }
}
