package com.bloom.bloomschool.users.service;

import com.bloom.bloomschool.student.exceptions.BadRequestException;
import com.bloom.bloomschool.users.repository.UserRepository;
import com.bloom.bloomschool.users.repository.RoleRepository;
import com.bloom.bloomschool.users.entity.Roles;
import com.bloom.bloomschool.users.entity.User;
import com.bloom.bloomschool.users.dto.RolesResponseDTO;
import com.bloom.bloomschool.users.dto.RolesRequestDTO;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolesService {
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;

    private Roles findEntity(UUID uuid) {
        return roleRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found!!"));
    }

    private Roles findEntity(String name) {
        return roleRepo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found!!"));
    }

    // Get All Roles
    public List<RolesResponseDTO> getRoles() {
        return roleRepo.findAll().stream().map(this::toResponse).toList();
    }

    // Get Role By UUID
    public RolesResponseDTO getRoleByUuid(UUID uuid) {
        return toResponse(findEntity(uuid));
    }

    // Get Role By Name
    public RolesResponseDTO getRoleByName(String name) {
        return toResponse(findEntity(name));
    }

    // Create Role
    @Transactional
    public void createRole(RolesRequestDTO req) {
        if (roleRepo.existsByName(req.getName())) {
            throw new BadRequestException("Role Does Not Exist!");
        }
        roleRepo.save(Roles.builder()
                .name(req.getName())
                .build());
    }

    // Update Role
    @Transactional
    public void updateRole(UUID uuid, RolesRequestDTO req) {
        Roles role = findEntity(uuid);
        if (req.getName() != null) {
            role.setName(req.getName());
        }
        roleRepo.save(role);
    }

    // Assign User to Role
    @Transactional
    public void addUserToRole(UUID roleUuid, UUID userUuid) {
        Roles role = findEntity(roleUuid);
        User user = userRepo.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found!!"));

        user.getRoles().add(role);
        userRepo.save(user);
    }

    // Remove User from Role
    @Transactional
    public void removeUserFromRole(UUID roleUuid, UUID userUuid) {
        Roles role = findEntity(roleUuid);
        User user = userRepo.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found!!"));

        user.getRoles().remove(role);
        userRepo.save(user);
    }

    // Delete Role
    @Transactional
    public void deleteRole(UUID uuid) {
        Roles role = findEntity(uuid);
        roleRepo.delete(role);
    }

    private RolesResponseDTO toResponse(Roles r) {
        return RolesResponseDTO.builder()
                .uuid(r.getUuid())
                .name(r.getName())
                .build();
    }
}