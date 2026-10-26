package com.bloom.bloomschool.users.service;

import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.users.dto.UserPermissionResponseDTO;
import com.bloom.bloomschool.users.entity.UserPermissions;
import com.bloom.bloomschool.users.repository.PermissionsRepository;
import com.bloom.bloomschool.users.repository.UserPermissionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserPermissionsService {
    private final UserPermissionsRepository userPermRepo;
    private final PermissionsRepository permissionRepo;

    private UserPermissions findEntity(UUID uuid){
        return userPermRepo.findByUuid(uuid)
                .orElseThrow(()-> new ResourceNotFoundException("User Permissions do not exist"));
    }

    public List<UserPermissionResponseDTO> findAllUserPermissions(){
        return userPermRepo.findAll().stream()
                .map(u -> UserPermissionResponseDTO.builder()
                        .uuid(u.getUuid())
                        .overrideType(u.getOverrideType())
                        .build())
                .toList();
    }

    public UserPermissionResponseDTO findUserPermissionsByUuid(UUID uuid){
        UserPermissions u = findEntity(uuid);

        return UserPermissionResponseDTO.builder()
                .uuid(u.getUuid())
                .overrideType(u.getOverrideType())
                .build();

    }
}
