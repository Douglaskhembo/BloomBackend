package com.bloom.bloomschool.school.service;

import com.bloom.bloomschool.school.dto.*;
import com.bloom.bloomschool.school.entity.Department;
import com.bloom.bloomschool.school.repository.DepartmentRepository;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepo;

    private Department findEntity(UUID uuid) {
        return departmentRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepo.findAll().stream()
                .map(d -> DepartmentResponseDTO.builder()
                        .uuid(d.getUuid())
                        .departmentName(d.getDepartmentName())
                        .description(d.getDescription())
                        .departmentCode(d.getDepartmentCode())
                        .build())
                .toList();
    }

    public DepartmentResponseDTO getDepartmentByUuid(UUID uuid) {
        Department d = findEntity(uuid);
        return DepartmentResponseDTO.builder()
                .uuid(d.getUuid())
                .departmentName(d.getDepartmentName())
                .departmentCode(d.getDepartmentCode())
                .description(d.getDescription())
                .build();
    }

    @Transactional
    public void createDepartment(DepartmentRequestDTO req) {
        if (departmentRepo.existsByDepartmentCode(req.getDepartmentCode())) {
            throw new RuntimeException("Department with this code already exists!!");
        }
        if (departmentRepo.existsByDepartmentName(req.getDepartmentName())) {
            throw new RuntimeException("Department with this name already exists!!");
        }
        departmentRepo.save(Department.builder()
                .departmentName(req.getDepartmentName())
                .departmentCode(req.getDepartmentCode())
                .description(req.getDescription())
                .build());
    }

    @Transactional
    public void updateDepartment(UUID uuid, DepartmentRequestDTO req) {
        Department d = findEntity(uuid);
        d.setDepartmentName(req.getDepartmentName());
        d.setDepartmentCode(req.getDepartmentCode());
        d.setDescription(req.getDescription());

        departmentRepo.save(d);
    }

    @Transactional
    public void deleteDepartment(UUID uuid) {
        Department d = findEntity(uuid);
        departmentRepo.delete(d);
    }

}