package com.bloom.bloomschool.grading.service;

import com.bloom.bloomschool.grading.dto.GradingEntriesRequest;
import com.bloom.bloomschool.grading.dto.GradingEntryRequest;
import com.bloom.bloomschool.grading.dto.GradingStructureRequest;
import com.bloom.bloomschool.grading.entity.GradingEntry;
import com.bloom.bloomschool.grading.entity.GradingStructure;
import com.bloom.bloomschool.grading.repository.GradingStructureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradingService {

    private final GradingStructureRepository gradingRepo;

    public List<GradingStructure> getAll() {
        return gradingRepo.findAllByOrderByGradeAsc();
    }

    @Transactional
    public GradingStructure create(GradingStructureRequest req) {
        if (gradingRepo.existsByGrade(req.getGrade()))
            throw new IllegalArgumentException("A grading structure for " + req.getGrade() + " already exists");
        return gradingRepo.save(GradingStructure.builder()
                .grade(req.getGrade())
                .entries(toEntries(req.getEntries()))
                .build());
    }

    @Transactional
    public GradingStructure replaceEntries(UUID uuid, GradingEntriesRequest req) {
        GradingStructure gs = gradingRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Grading structure not found"));
        gs.setEntries(toEntries(req.getEntries()));
        return gradingRepo.save(gs);
    }

    @Transactional
    public void delete(UUID uuid) {
        GradingStructure gs = gradingRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Grading structure not found"));
        gradingRepo.delete(gs);
    }

    private List<GradingEntry> toEntries(List<GradingEntryRequest> entries) {
        return entries.stream()
                .map(e -> GradingEntry.builder()
                        .label(e.getLabel())
                        .minScore(e.getMinScore())
                        .maxScore(e.getMaxScore())
                        .points(e.getPoints())
                        .remark(e.getRemark())
                        .build())
                .toList();
    }
}
