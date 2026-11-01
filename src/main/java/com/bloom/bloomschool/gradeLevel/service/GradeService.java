package com.bloom.bloomschool.gradeLevel.service;

import com.bloom.bloomschool.gradeLevel.dto.GradeDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.gradeLevel.repository.GradeRepository;
import com.bloom.bloomschool.student.enums.ApplicationStage;
import com.bloom.bloomschool.student.enums.StudentStatus;
import com.bloom.bloomschool.student.repository.AdmissionsRepository;
import com.bloom.bloomschool.student.repository.StudentRepository;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository repo;
    private final AdmissionsRepository admissionsRepo;
    private final StudentRepository studentRepo;

    @Transactional(readOnly = true)
    public List<GradeDto> get_all_grades(){
        List<GradeEntity> gradeEntity = repo.findAll();
        if(CollectionUtils.isEmpty(gradeEntity)){
            return List.of();
        }
        return gradeEntity.stream()
                .map(this::convertToDto).toList();
    }

    @Transactional(readOnly = true)
    public GradeDto get_grade(UUID uuid) {
        return  convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No grade found")));
    }

    private GradeDto convertToDto(GradeEntity body){
        return GradeDto.builder()
                .uuid(body.getUuid())
                .name(body.getName())
                .displayOrder(body.getDisplayOrder())
                .capacity(body.getCapacity())
                .active(body.isActive())
                .streamName(body.getStreamName())
                .streamCapacity(body.getStreamCapacity())
                .streams(body.getStreams())
                .uuid(body.getUuid())
                .build();
    }
    @Transactional
    public void createGrade(GradeDto req){
        if(repo.existsByName(req.getName())){
            throw new RuntimeException("A grade with this name exists");
        }

        repo.save(GradeEntity.builder()
                .name(req.getName())
                .displayOrder(req.getDisplayOrder())
                .capacity(req.getCapacity())
                .active(req.isActive())
                .streamName(req.getStreamName())
                .streamCapacity(req.getStreamCapacity())
                .streams(req.getStreams())
                .build());

    }

    @Transactional
    public void update(UUID uuid, GradeDto req){
        GradeEntity existingGrade = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No grade with this uuid found"));
        if(repo.existsByNameAndUuidNot(req.getName(), uuid)){
            throw new RuntimeException("A grade with this name exists");
        }
        existingGrade.setName(req.getName());
        existingGrade.setDisplayOrder(req.getDisplayOrder());
        existingGrade.setCapacity(req.getCapacity());
        existingGrade.setActive(req.isActive());
        existingGrade.setStreamName(req.getStreamName());
        existingGrade.setStreamCapacity(req.getStreamCapacity());
        existingGrade.setStreams(req.getStreams());

        repo.save(existingGrade);
    }

    @Transactional
    public void toggleStatus(UUID uuid){
        GradeEntity existingGrade = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No grade with this uuid found"));
        existingGrade.setActive(!existingGrade.isActive());
        repo.save(existingGrade);
    }

    @Transactional
    public void deleteGrade(UUID uuid){
        GradeEntity existingGrade = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("System error occurred please contact Admin"));
        repo.delete(existingGrade);
    }

    public void isStreamFull(GradeEntity gradeEntity, String streamName) {
        if(gradeEntity == null) return;

        Integer capacity = checkCapacity(gradeEntity, streamName);
        long current = (streamName == null || streamName.isEmpty())
                ? studentRepo.getGradeCapacity(gradeEntity.getUuid(), StudentStatus.ACTIVE)
                : studentRepo.getStreamCapacity(gradeEntity.getUuid(), streamName, StudentStatus.ACTIVE);

        if (current >= capacity){
            throw new RuntimeException("Full capacity");
        }

    }
    private Integer checkCapacity(GradeEntity gradeEntity, String streamName){
        if(gradeEntity.getStreams() <= 1) return gradeEntity.getCapacity();

        List<String> names = gradeEntity.getStreamName();
        List<Integer> capacities = gradeEntity.getStreamCapacity();

        int capacity = names.indexOf(streamName);
        if(capacity < 0 || capacity >= capacities.size()) throw new RuntimeException("Invalid stream: " + streamName);

        return capacities.get(capacity);
    }

}
