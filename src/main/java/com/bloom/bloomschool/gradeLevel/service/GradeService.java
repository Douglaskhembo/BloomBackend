package com.bloom.bloomschool.gradeLevel.service;

import com.bloom.bloomschool.gradeLevel.dto.GradeDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.gradeLevel.repository.GradeRepository;
import com.bloom.bloomschool.student.enums.ApplicationStage;
import com.bloom.bloomschool.student.repository.AdmissionsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository repo;
    private final AdmissionsRepository admissionsRepo;

    public List<GradeDto> get_all_grades(){
        List<GradeEntity> gradeEntity = repo.findAll();
        if(CollectionUtils.isEmpty(gradeEntity)){
            return List.of();
        }
        return gradeEntity.stream()
                .map(this::convertToDto).toList();
    }

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
                .isActive(body.getIsActive())
                .streamName(body.getStreamName())
                .streamCapacity(body.getStreamCapacity())
                .streams(body.getStreams())
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
                .isActive(req.getIsActive())
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
        existingGrade.setIsActive(req.getIsActive());
        existingGrade.setStreamName(req.getStreamName());
        existingGrade.setStreamCapacity(req.getStreamCapacity());
        existingGrade.setStreams(req.getStreams());

        repo.save(existingGrade);
    }

    @Transactional
    public void toggleStatus(UUID uuid){
        GradeEntity existingGrade = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No grade with this uuid found"));
        existingGrade.setIsActive(!existingGrade.getIsActive());
        repo.save(existingGrade);
    }

    @Transactional
    public void deleteGrade(UUID uuid){
        GradeEntity existingGrade = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No grade with this uuid found"));
        repo.delete(existingGrade);
    }

    public boolean isStreamFull(UUID uuid, String streamName) {
        GradeEntity existingGrade = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No grade with this uuid found"));

        List<String> streamNames = existingGrade.getStreamName();
        List<Integer> streamCapacities = existingGrade.getStreamCapacity();

        if (streamNames == null || streamNames.isEmpty()) {
            throw new RuntimeException("No streams saved");
        }
        if (streamCapacities == null || streamCapacities.isEmpty()) {
            throw new RuntimeException("Stream capacity not filled");
        }

        int streamIndex = streamNames.indexOf(streamName);
        if (streamIndex == -1) {
            throw new RuntimeException("The stream '" + streamName + "' does not exist for this grade");
        }

        if (streamIndex >= streamCapacities.size()) {
            throw new RuntimeException("Configuration error: Stream capacity mapping missing for index " + streamIndex);
        }

        int streamCapacity = streamCapacities.get(streamIndex);
        int occupiedStreamCapacity = admissionsRepo.getOccupiedStreamCapacity(uuid, streamName, ApplicationStage.ENROLLED);

        if (occupiedStreamCapacity >= streamCapacity) {
            throw new RuntimeException("Stream full, contact admin /assign different stream");
        }

        return false;
    }


}
