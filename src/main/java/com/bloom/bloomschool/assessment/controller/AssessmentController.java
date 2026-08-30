package com.bloom.bloomschool.assessment.controller;


import com.bloom.bloomschool.assessment.dto.AssessmentDto;
import com.bloom.bloomschool.assessment.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assessment")
@RequiredArgsConstructor
public class AssessmentController {
    private final AssessmentService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<AssessmentDto>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get-assessment/{uuid}")
    public ResponseEntity<AssessmentDto> getAssessment(@PathVariable UUID uuid) throws BadRequestException{
        return ResponseEntity.ok(service.getAssessment(uuid));
    }

    @PostMapping("/create-assessment")
    public ResponseEntity<?> create(@RequestBody AssessmentDto req){
        service.createAssessment(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update-assessment/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody AssessmentDto req){
        service.updateAssessment(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete-assessment/{uuid}")
    public ResponseEntity<?> delete(@PathVariable UUID uuid){
        service.deleteAssessment(uuid);
        return ResponseEntity.noContent().build();
    }


}
