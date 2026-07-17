package com.bloom.bloomschool.auth.service;

import com.bloom.bloomschool.auth.dto.Requests.CreateRoleRequest;
import com.bloom.bloomschool.auth.dto.Responses.RoleResponse;
import com.bloom.bloomschool.auth.model.Permission;
import com.bloom.bloomschool.auth.model.Role;
import com.bloom.bloomschool.auth.repo.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public void createRole(CreateRoleRequest request) {
        if (roleRepository.existsByName(request.getRoleName()))
            throw new IllegalArgumentException("Role already exists");
        roleRepository.save(Role.builder().name(request.getRoleName()).build());
    }

    public RoleResponse getRoleByUuid(UUID uuid) {
        return toResponse(roleRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Role not found")));
    }

    public RoleResponse getRoleByName(String name) {
        return toResponse(roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role not found")));
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    public void updateRole(UUID uuid, CreateRoleRequest request) {
        Role role = roleRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        role.setName(request.getRoleName());
        roleRepository.save(role);
    }

    public void deleteRole(UUID uuid) {
        Role role = roleRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        try {
            roleRepository.delete(role);
            roleRepository.flush();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot delete role — it is assigned to users");
        }
    }

    private RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .roleUuid(role.getUuid())
                .roleName(role.getName())
                .permissions(role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()))
                .build();
    }
}
