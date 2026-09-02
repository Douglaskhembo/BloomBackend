package com.bloom.bloomschool.calender.service;

import com.bloom.bloomschool.calender.dto.TermPeriodRequestDTO;
import com.bloom.bloomschool.calender.dto.TermPeriodResponseDTO;
import com.bloom.bloomschool.calender.entity.TermPeriod;
import com.bloom.bloomschool.calender.repository.TermPeriodRepository;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TermPeriodService {
    private final TermPeriodRepository termRepo;

    private TermPeriod findEntity(UUID uuid){
        return termRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Term Period does not exist!!"));
    }

    public List<TermPeriodResponseDTO> getAllTermPeriods(){
        return termRepo.findAll().stream()
                .map(t -> TermPeriodResponseDTO.builder()
                        .uuid(t.getUuid())
                        .term(t.getTerm())
                        .academicYear(t.getAcademicYear())
                        .startDate(t.getStartDate())
                        .endDate(t.getEndDate())
                        .build())
                .toList();
    }

    public TermPeriodResponseDTO findTermPeriodByUuid(UUID uuid){
        TermPeriod t = findEntity(uuid);
        return TermPeriodResponseDTO.builder()
                .uuid(t.getUuid())
                .term(t.getTerm())
                .academicYear(t.getAcademicYear())
                .startDate(t.getStartDate())
                .endDate(t.getEndDate())
                .build();
    }

    @Transactional
    public void createTermPeriod(TermPeriodRequestDTO req){
        if (termRepo.existsByTermAndAcademicYear(req.getTerm(), req.getAcademicYear())){
            throw new RuntimeException("Term period already exists!!");
        }

        termRepo.save(TermPeriod.builder()
                .term(req.getTerm())
                .academicYear(req.getAcademicYear())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .build());
    }

    @Transactional
    public void updateTermPeriod(UUID uuid, TermPeriodRequestDTO req){
        TermPeriod t = findEntity(uuid);
        t.setTerm(req.getTerm());
        t.setAcademicYear(req.getAcademicYear());
        t.setStartDate(req.getStartDate());
        t.setEndDate(req.getEndDate());

        termRepo.save(t);
    }

    @Transactional
    public void deleteTermPeriod(UUID uuid){
        TermPeriod t = findEntity(uuid);
        termRepo.delete(t);
    }
}