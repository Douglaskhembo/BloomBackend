package com.bloom.bloomschool.student.controller;

import com.bloom.bloomschool.student.dto.AdmissionsDto;
import com.bloom.bloomschool.student.enums.ApplicationStage;
import com.bloom.bloomschool.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admissions")
@RequiredArgsConstructor
public class AdmissionsController {
    private final StudentService service;

    @GetMapping("/allAdmissions")
    public ResponseEntity<List<AdmissionsDto>> getAll() {
        return ResponseEntity.ok(service.getAllAdmissions());
    }

    @GetMapping("/admission/{uuid}")
    public ResponseEntity<AdmissionsDto> getAdmission(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getAdmission(uuid));
    }

    @PostMapping("/createAdmission")
    public ResponseEntity<?> create(@RequestBody AdmissionsDto req) {
        service.createAdmission(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/updateAdmission/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody AdmissionsDto req) {
        service.updateAdmission(uuid, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/deleteAdmission/{uuid}")
    public ResponseEntity<?> deleteAdmission(@PathVariable UUID uuid) {
        service.deleteAdmission(uuid);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/updateAdmissionStatus/{uuid}")
    public ResponseEntity<?> updateAdmissionStatus(@PathVariable UUID uuid, @RequestBody ApplicationStage stage, @RequestBody LocalDate joinDate){
        service.updateAdmissionStage(uuid, stage, joinDate);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
