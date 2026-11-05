package com.bloom.bloomschool.subject.controller;

import com.bloom.bloomschool.subject.dto.SubjectRequestDto;
import com.bloom.bloomschool.subject.dto.SubjectResponseDto;
import com.bloom.bloomschool.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService service;

    @GetMapping("/allSubjects")
    public ResponseEntity<List<SubjectResponseDto>> get_all_subjects() {
        return ResponseEntity.ok(service.get_all_subjects());
    }

    @GetMapping("/subject/{uuid}")
    public ResponseEntity<List<SubjectResponseDto>> get_subject(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.get_subject(uuid));
    }

    @PostMapping("/createSubject")
    public ResponseEntity<?> create(@RequestBody SubjectRequestDto req) {
        service.createSubject(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/updateSubject/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody SubjectRequestDto req) {
        service.update(uuid, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/toggleStatus/{uuid}")
    public ResponseEntity<?> toggleStatus(@PathVariable UUID uuid) {
        service.toggleStatus(uuid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteAdmission/{uuid}")
    public ResponseEntity<?> deleteSubject(@PathVariable UUID uuid) {
        service.deleteSubject(uuid);
        return ResponseEntity.noContent().build();
    }
}
