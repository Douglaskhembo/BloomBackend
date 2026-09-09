package com.bloom.bloomschool.subject.controller;


import com.bloom.bloomschool.subject.dto.SubjectDto;
import com.bloom.bloomschool.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
//Test on postman
@RestController
@RequestMapping("/api/v1/subject")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService service;

    @GetMapping("/allSubjects")
    public ResponseEntity<List<SubjectDto>> get_all_subjects(){return ResponseEntity.ok(service.get_all_subjects());}

    @GetMapping("/subject/{uuid}")
    public ResponseEntity<SubjectDto> get_subject(@PathVariable UUID uuid) throws BadRequestException {
        return ResponseEntity.ok(service.get_subject(uuid));
    }

    @PostMapping("createSubject")
    public ResponseEntity<?> create(@RequestBody SubjectDto req){
        service.createSubject(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("updateSubject/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody SubjectDto req){
        service.update(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("toggleStatus/{uuid}")
    public ResponseEntity<?> toggleStatus(@PathVariable UUID uuid){
        service.toggleStatus(uuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("deleteSubject/{uuid}")
    public ResponseEntity<?> deleteSubject(@PathVariable UUID uuid){
        service.deleteSubject(uuid);
        return ResponseEntity.noContent().build();
    }
}
