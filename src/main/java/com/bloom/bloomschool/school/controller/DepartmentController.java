package com.bloom.bloomschool.school.controller;

import com.bloom.bloomschool.school.dto.DepartmentRequestDTO;
import com.bloom.bloomschool.school.dto.DepartmentResponseDTO;
import com.bloom.bloomschool.school.service.DepartmentService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    // Get all departments
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    // Get single department by UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(departmentService.getDepartmentByUuid(uuid));
    }

    // Create a department
    @PostMapping
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentRequestDTO request) {
        departmentService.createDepartment(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Update department data
    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody DepartmentRequestDTO req) {
        departmentService.updateDepartment(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // Delete branch
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteDepartment(@PathVariable UUID uuid) {
        departmentService.deleteDepartment(uuid);
        return ResponseEntity.noContent().build();
    }
}
