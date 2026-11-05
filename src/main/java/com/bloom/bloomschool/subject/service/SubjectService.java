package com.bloom.bloomschool.subject.service;

import com.bloom.bloomschool.gradeLevel.dto.GradeResponseDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.gradeLevel.repository.GradeRepository;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.subject.dto.SubjectRequestDto;
import com.bloom.bloomschool.subject.dto.SubjectResponseDto;
import com.bloom.bloomschool.subject.entity.SubjectEntity;
import com.bloom.bloomschool.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repo;
    private final GradeRepository gradeRepo;

    public List<SubjectResponseDto> get_all_subjects(){
        List<SubjectResponseDto> dto = new ArrayList<>();
        List<Object[]> subjects = repo.findAllSubjects();
        for(Object[] obj :subjects){
            SubjectResponseDto subjectResponseDto = new SubjectResponseDto();
            subjectResponseDto.setUuid(obj[0] != null ? UUID.fromString(obj[0].toString()) : null);
            subjectResponseDto.setName((String)obj[1]);
            subjectResponseDto.setSubjectCode((String)obj[2]);
            subjectResponseDto.setIsActive((Boolean) obj[3]);
            subjectResponseDto.setGrades(new ArrayList<>());
            if(obj[4] != null){
                GradeResponseDto g = new GradeResponseDto();
                g.setUuid((UUID)obj[4]);
                g.setName((String) obj[5]);
                g.setDisplayOrder(obj[6] == null ? 0 : ((int) obj[6]));
                g.setCapacity(obj[7] == null ? 0 : ((int) obj[7]));
                g.setActive(obj[8] != null && (Boolean) obj[8]);
                g.setStreams(obj[9] == null ? 0 : ((int) obj[9]));
                subjectResponseDto.getGrades().add(g);
            }

            dto.add(subjectResponseDto);
        }
        return dto;
    }

    public List<SubjectResponseDto> get_subject(UUID uuid){
        SubjectEntity subject =repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        List<SubjectResponseDto> dto = new ArrayList<>();
        List<Object[]> subjects = repo.findSubjectByUuid(subject.getUuid());
        for(Object[] obj :subjects){
            SubjectResponseDto subjectResponseDto = new SubjectResponseDto();
            subjectResponseDto.setUuid(obj[0] != null ? UUID.fromString(obj[0].toString()) : null);
            subjectResponseDto.setName((String)obj[1]);
            subjectResponseDto.setSubjectCode((String)obj[2]);
            subjectResponseDto.setIsActive((Boolean) obj[3]);
            subjectResponseDto.setGrades(new ArrayList<>());
            if(obj[4] != null){
                GradeResponseDto g = new GradeResponseDto();
                g.setUuid((UUID)obj[4]);
                g.setName((String) obj[5]);
                g.setDisplayOrder(obj[6] == null ? 0 : ((int) obj[6]));
                g.setCapacity(obj[7] == null ? 0 : ((int) obj[7]));
                g.setActive(obj[8] != null && (Boolean) obj[8]);
                g.setStreams(obj[9] == null ? 0 : ((int) obj[9]));
                subjectResponseDto.getGrades().add(g);
            }

            dto.add(subjectResponseDto);
        }
        return dto;
    }

    private SubjectResponseDto convertToDto(SubjectEntity subject){
        List<GradeResponseDto> gradeDtos = subject.getGrades().stream()
                .map(grade -> GradeResponseDto.builder()
                        .name(grade.getName())
                        .displayOrder(grade.getDisplayOrder())
                        .capacity(grade.getCapacity())
                        .isActive(grade.isActive())
                        .streamName(grade.getStreamName())
                        .streamCapacity(grade.getStreamCapacity())
                        .streams(grade.getStreams())
                        .build())
                .toList();
        return SubjectResponseDto.builder()
                .uuid(subject.getUuid())
                .name(subject.getName())
                .subjectCode(subject.getSubjectCode())
                .isActive(subject.getIsActive())
                .grades(gradeDtos)
                .build();
    }
    @Transactional
    public void createSubject(SubjectRequestDto req){
        if(repo.existsByName(req.getName())){
            throw new RuntimeException("A subject with this name exists");
        }

        if(repo.existsBySubjectCode(req.getSubjectCode())){
            throw new RuntimeException("A subject with this code exists");
        }
        repo.save(SubjectEntity.builder()
                        .name(req.getName())
                        .subjectCode(req.getSubjectCode())
                        .isActive(req.getIsActive())
                        .grades(convert(req.getGradeUuids()))
                .build());

    }

    private List<GradeEntity> convert(List<UUID> uuids){
        if (uuids == null || uuids.isEmpty()) return new ArrayList<>();

        List<UUID> distinct = uuids.stream().distinct().toList();
        List<GradeEntity> found = gradeRepo.findAllByUuidIn(distinct);

        if (found.size() != distinct.size()) {
            Set<UUID> foundIds = found.stream().map(GradeEntity::getUuid).collect(Collectors.toSet());
            List<UUID> missing = distinct.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade(s) not found: " + missing);
        }
        return new ArrayList<>(found);
    }

    @Transactional
    public void update(UUID uuid, SubjectRequestDto req){
        SubjectEntity existingSubject = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No subject with this uuid found"));
        if(repo.existsByNameAndUuidNot(req.getName(), uuid)){
            throw new RuntimeException("A subject with this name exists");
        }

        if(repo.existsBySubjectCodeAndUuidNot(req.getSubjectCode(), uuid)){
            throw new RuntimeException("A subject with this code exists");
        }

        existingSubject.setName(req.getName());
        existingSubject.setSubjectCode(req.getSubjectCode());
        existingSubject.setGrades(convert(req.getGradeUuids()));
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
