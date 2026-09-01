package com.bloom.bloomschool.calender.service;

import com.bloom.bloomschool.calender.dto.SchoolEventRequestDTO;
import com.bloom.bloomschool.calender.dto.SchoolEventResponseDTO;
import com.bloom.bloomschool.calender.entity.SchoolEvent;
import com.bloom.bloomschool.calender.repository.SchoolEventRepository;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolEventService {
    private final SchoolEventRepository schoolEventRepo;

    private SchoolEvent findEntity(UUID uuid){
        return schoolEventRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("The event is Unavailable"));
    }

    public List<SchoolEventResponseDTO> getAllSchoolEvents(){
        return schoolEventRepo.findAll().stream()
                .map(s -> SchoolEventResponseDTO.builder()
                        .uuid(s.getUuid())
                        .eventName(s.getEventName())
                        .startDate(s.getStartDate())
                        .endDate(s.getEndDate())
                        .active(s.isActive())
                        .build())
                .toList();
    }

    public SchoolEventResponseDTO getSchoolEventByUuid(UUID uuid){
        SchoolEvent s = findEntity(uuid);
        return SchoolEventResponseDTO.builder()
                .uuid(s.getUuid())
                .eventName(s.getEventName())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .active(s.isActive())
                .build();
    }

    @Transactional
    public void createSchoolEvent(SchoolEventRequestDTO req) {
        if(schoolEventRepo.existsByEventNameAndStartDate(req.getEventName(), req.getStartDate())){
            throw new RuntimeException("This event already exists");
        }
        schoolEventRepo.save(SchoolEvent.builder()
                .eventName(req.getEventName())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .active(true)
                .build());
    }

    @Transactional
    public void updateSchoolEvent(UUID uuid, SchoolEventRequestDTO req){
        SchoolEvent s = findEntity(uuid);
        s.setEventName(req.getEventName());
        s.setStartDate(req.getStartDate());
        s.setEndDate(req.getEndDate());

        schoolEventRepo.save(s);
    }

    @Transactional
    public void deleteSchoolEvent(UUID uuid) {
        SchoolEvent s = findEntity(uuid);
        schoolEventRepo.delete(s);
    }

}
