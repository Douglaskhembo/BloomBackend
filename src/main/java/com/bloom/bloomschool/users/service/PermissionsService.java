package com.bloom.bloomschool.users.service;

import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.users.dto.PermissionsRequestDTO;
import com.bloom.bloomschool.users.dto.PermissionsResponseDTO;
import com.bloom.bloomschool.users.entity.Modules;
import com.bloom.bloomschool.users.entity.Permissions;
import com.bloom.bloomschool.users.repository.ModulesRepository;
import com.bloom.bloomschool.users.repository.PermissionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PermissionsService {
    private final PermissionsRepository permRepo;
    private final ModulesRepository moduleRepo;

    private Permissions findEntity(UUID uuid){
        return permRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Permissions Are Unavailable"));
    }

    private Permissions findEntity(String name){
        return permRepo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Permissions Are Unavailable!"));
    }

    public List<PermissionsResponseDTO> getPermissions(){
        return permRepo.findAll().stream()
                .map(p -> PermissionsResponseDTO.builder()
                        .uuid(p.getUuid())
                        .name(p.getName())
                        .description(p.getDescription())
                        .build())
                .toList();
    }

    public PermissionsResponseDTO getByUuid(UUID uuid){
        Permissions p = findEntity(uuid);

        return PermissionsResponseDTO.builder()
                .uuid(p.getUuid())
                .name(p.getName())
                .description(p.getDescription())
                .build();
    }

    public PermissionsResponseDTO getByName(String name){
        Permissions p = findEntity(name);

        return PermissionsResponseDTO.builder()
                .uuid(p.getUuid())
                .name(p.getName())
                .description(p.getDescription())
                .build();
    }

    @Transactional
    public void createPermission(PermissionsRequestDTO req){
        Modules module = moduleRepo.findByUuid(req.getModuleUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Module Not Found"));
        Permissions permission = Permissions.builder()
                .name(req.getName())
                .description(req.getDescription())
                .module(module)
                .build();

        permRepo.save(permission);
    }

    @Transactional
    public void updatePermissions(UUID uuid, PermissionsRequestDTO req){
        Permissions p = findEntity(uuid);

        p.setName(req.getName());
        p.setDescription(req.getDescription());
        permRepo.save(p);
    }

    @Transactional
    public void deletePermissions(UUID uuid){
        Permissions p = findEntity(uuid);
        permRepo.delete(p);
    }
}