package com.bloom.bloomschool.subject.service;

import com.bloom.bloomschool.gradeLevel.dto.GradeDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.gradeLevel.repository.GradeRepository;
import com.bloom.bloomschool.subject.dto.SubjectDto;
import com.bloom.bloomschool.subject.entity.SubjectEntity;
import com.bloom.bloomschool.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repo;
    private final GradeRepository gradeRepo;

    public List<SubjectDto> get_all_subjects(){
        List<SubjectEntity> subjectEntities = repo.findAll();
        if (CollectionUtils.isEmpty(subjectEntities)) {
            return List.of();
        }
        return subjectEntities.stream().map(this::convertToDto).toList();
    }

    public SubjectDto get_subject(UUID uuid) throws BadRequestException {
        return convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new BadRequestException("No subject found")));
    }

    private SubjectDto convertToDto(SubjectEntity subject){
        List<GradeDto> gradeDtos = subject.getGrades().stream()
                .map(grade -> GradeDto.builder()
                        .name(grade.getName())
                        .displayOrder(grade.getDisplayOrder())
                        .capacity(grade.getCapacity())
                        .isActive(grade.getIsActive())
                        .streamName(grade.getStreamName())
                        .streamCapacity(grade.getStreamCapacity())
                        .streams(grade.getStreams())
                        .build())
                .toList();
        return SubjectDto.builder()
                .name(subject.getName())
                .subjectCode(subject.getSubjectCode())
                .status(subject.getIsActive())
                .gradeDtos(gradeDtos)
                .build();
    }
    @Transactional
    public void createSubject(SubjectDto req){
        if(repo.existsByName(req.getName())){
            throw new RuntimeException("A subject with this name exists");
        }

        if(repo.existsBySubjectCode(req.getSubjectCode())){
            throw new RuntimeException("A subject with this code exists");
        }
        repo.save(SubjectEntity.builder()
                        .name(req.getName())
                        .subjectCode(req.getSubjectCode())
                        .isActive(req.isStatus())
                        .grades(convert(req.getGradesUuid()))
                .build());

    }

    private List<GradeEntity> convert(List<UUID> uuids){
        if (uuids == null || uuids.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(gradeRepo.findAllByUuidIn(uuids));
    }

    @Transactional
    public void update(UUID uuid, SubjectDto req){
        SubjectEntity existingSubject = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No subject with this uuid found"));
        if(repo.existsByName(req.getName())){
            throw new RuntimeException("A subject with this name exists");
        }

        if(repo.existsBySubjectCode(req.getSubjectCode())){
            throw new RuntimeException("A subject with this code exists");
        }

        existingSubject.setName(req.getName());
        existingSubject.setSubjectCode(req.getSubjectCode());
        existingSubject.setGrades(convert(req.getGradesUuid()));
        repo.save(existingSubject);
    }

    @Transactional
    public void toggleStatus(UUID uuid){
        SubjectEntity existingSubject = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No subject with this uuid found"));
        existingSubject.setIsActive(!existingSubject.getIsActive());
        repo.save(existingSubject);
    }

    @Transactional
    public void deleteSubject(UUID uuid){
        SubjectEntity existingSubject = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No subject with this uuid found"));
        repo.delete(existingSubject);
    }

}
