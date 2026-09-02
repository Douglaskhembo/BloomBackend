package com.bloom.bloomschool.school.controller;

import com.bloom.bloomschool.school.dto.SchoolInformationRequestDTO;
import com.bloom.bloomschool.school.dto.SchoolInformationResponseDTO;
import com.bloom.bloomschool.school.service.SchoolInformationService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/institutions")
@RequiredArgsConstructor

public class SchoolInformationController {
    private final SchoolInformationService schoolInformationService;

    // Get all schools
    @GetMapping
    public ResponseEntity<List<SchoolInformationResponseDTO>> getAllSchools() {
        return ResponseEntity.ok(schoolInformationService.getAllSchools());
    }

    // Get single school by UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<SchoolInformationResponseDTO> getSchoolByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(schoolInformationService.getSchoolByUuid(uuid));
    }

    // Create a school
    @PostMapping
    public ResponseEntity<?> createSchool(@RequestBody SchoolInformationRequestDTO request) {
        schoolInformationService.createSchool(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Update school data
    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody SchoolInformationRequestDTO req) {
        schoolInformationService.updateSchool(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{uuid}/toggleBranch")
    public ResponseEntity<?> toggleBranch(@PathVariable UUID uuid) {
        schoolInformationService.toggleBranchStatus(uuid);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{uuid}/toggleDepartment")
    public ResponseEntity<?> toggleDepartment(@PathVariable UUID uuid) {
        schoolInformationService.toggleDepartmentStatus(uuid);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // Delete school
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteSchool(@PathVariable UUID uuid) {
        schoolInformationService.deleteSchool(uuid);
        return ResponseEntity.noContent().build();
    }
}
