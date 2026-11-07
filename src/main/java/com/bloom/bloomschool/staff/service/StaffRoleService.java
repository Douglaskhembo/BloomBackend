package com.bloom.bloomschool.staff.service;


import com.bloom.bloomschool.staff.dto.StaffRoleRequestDto;
import com.bloom.bloomschool.staff.dto.StaffRoleResponseDto;
import com.bloom.bloomschool.staff.entity.StaffRole;
import com.bloom.bloomschool.staff.repository.StaffRoleRepository;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffRoleService {
    private final StaffRoleRepository repo;

    public List<StaffRoleResponseDto> getAllRoles(){
        List<StaffRole> staffRoles = repo.findAll();
        if(CollectionUtils.isEmpty(staffRoles)){
            return new ArrayList<>();
        }
        return staffRoles.stream().map(this::convertToDto).toList();
    }

    public StaffRoleResponseDto getStaffRole(UUID uuid){
        StaffRole staffRole = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Staff Role not found"));
        return convertToDto(staffRole);
    }
    private StaffRoleResponseDto convertToDto(StaffRole body){
        return StaffRoleResponseDto.builder()
                .uuid(body.getUuid())
                .name(body.getName())
                .description(body.getDescription())
                .status(body.isStatus())
                .build();
    }

    public void createStaffRole(StaffRoleRequestDto req){
        repo.save(StaffRole.builder()
                .name(req.getName())
                .description(req.getDescription())
                .status(req.isStatus())
                .build());
    }

    public void updateStaffRole(UUID uuid, StaffRoleRequestDto req){
        StaffRole existingStaffRole = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Staff Role not found"));

        existingStaffRole.setName(req.getName());
        existingStaffRole.setDescription(req.getDescription());
        repo.save(existingStaffRole);
    }

    public void toggleStaffRoleStatus(UUID uuid){
        StaffRole existingStaffRole = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Staff Role not found"));
        existingStaffRole.setStatus(!existingStaffRole.isStatus());
        repo.save(existingStaffRole);
    }
    public void deleteStaffRole(UUID uuid){
        StaffRole existingStaffRole = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Staff Role not found"));

        repo.delete(existingStaffRole);
    }
}
