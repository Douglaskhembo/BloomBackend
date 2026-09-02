package com.bloom.bloomschool.calender.controller;

import com.bloom.bloomschool.calender.dto.SchoolEventResponseDTO;
import com.bloom.bloomschool.calender.dto.SchoolEventRequestDTO;
import com.bloom.bloomschool.calender.service.SchoolEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/calender/events")
@RequiredArgsConstructor

public class SchoolEventController {
    private final SchoolEventService eventService;

    @GetMapping
    public ResponseEntity<List<SchoolEventResponseDTO>> getAllSchoolEvents(){
        return ResponseEntity.ok(eventService.getAllSchoolEvents());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SchoolEventResponseDTO> getSchoolEventByUuid(@PathVariable UUID uuid){
        return ResponseEntity.ok(eventService.getSchoolEventByUuid(uuid));
    }

    @PostMapping
    public ResponseEntity<?> createSchoolEvent(@RequestBody SchoolEventRequestDTO request) {
        eventService.createSchoolEvent(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody SchoolEventRequestDTO req) {
        eventService.updateSchoolEvent(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteSchoolEvent(@PathVariable UUID uuid) {
        eventService.deleteSchoolEvent(uuid);
        return ResponseEntity.noContent().build();
    }
}
