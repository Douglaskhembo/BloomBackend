package com.bloom.bloomschool.auth.service;

import com.bloom.bloomschool.auth.dto.PermissionsBean;
import com.bloom.bloomschool.auth.dto.Responses.PermissionResponse;
import com.bloom.bloomschool.auth.model.Permission;
import com.bloom.bloomschool.auth.model.Role;
import com.bloom.bloomschool.auth.model.SysModule;
import com.bloom.bloomschool.auth.repo.PermissionRepository;
import com.bloom.bloomschool.auth.repo.RoleRepository;
import com.bloom.bloomschool.auth.repo.SysModuleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final SysModuleRepository moduleRepository;

    public void createPermission(PermissionsBean request) {
        if (permissionRepository.findByName(request.getName()).isPresent())
            throw new IllegalArgumentException("Permission already exists");
        SysModule module = moduleRepository.findByUuid(request.getModuleUuid())
                .orElseThrow(() -> new EntityNotFoundException("Module not found"));
        permissionRepository.save(Permission.builder()
                .name(request.getName()).module(module)
                .permDesc(request.getPermDesc()).accessType(request.getAccessType())
                .build());
    }

    public PermissionResponse getByUuid(UUID uuid) {
        return toResponse(permissionRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found")));
    }

    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<PermissionResponse> getByModule(UUID roleUuid, UUID moduleUuid) {
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        SysModule module = moduleRepository.findByUuid(moduleUuid)
                .orElseThrow(() -> new EntityNotFoundException("Module not found"));
        Set<Permission> rolePerms = role.getPermissions();
        return module.getPermissions().stream()
                .map(p -> toResponseWithGrant(p, rolePerms)).toList();
    }

    public void updatePermission(UUID uuid, String name) {
        Permission p = permissionRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found"));
        p.setName(name);
        permissionRepository.save(p);
    }

    public void deletePermission(UUID uuid) {
        Permission p = permissionRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found"));
        permissionRepository.delete(p);
    }

    @Transactional
    public void grantPermissionToRole(PermissionsBean bean) {
        Role role = roleRepository.findByUuid(bean.getRoleUuid())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        Permission permission = permissionRepository.findByUuid(bean.getPermissionUuid())
                .orElseThrow(() -> new EntityNotFoundException("Permission not found"));
        if (role.getPermissions().stream().anyMatch(p -> p.getId().equals(permission.getId())))
            throw new IllegalArgumentException("Permission already granted to this role");
        role.getPermissions().add(permission);
        roleRepository.save(role);
    }

    @Transactional
    public void revokePermissionFromRole(PermissionsBean bean) {
        Role role = roleRepository.findByUuid(bean.getRoleUuid())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        Permission permission = permissionRepository.findByUuid(bean.getPermissionUuid())
                .orElseThrow(() -> new EntityNotFoundException("Permission not found"));
        if (role.getPermissions().stream().noneMatch(p -> p.getId().equals(permission.getId())))
            throw new IllegalArgumentException("Permission not found on this role");
        role.getPermissions().remove(permission);
        roleRepository.save(role);
    }

    public Set<Permission> permissionsListMapper(Set<Long> ids) {
        List<Permission> permissions = permissionRepository.findAllByIdIn(ids);
        if (permissions.size() != ids.size())
            throw new EntityNotFoundException("Some permission IDs were not found");
        return new HashSet<>(permissions);
    }

    private PermissionResponse toResponse(Permission p) {
        return PermissionResponse.builder()
                .permUuid(p.getUuid()).name(p.getName())
                .permDesc(p.getPermDesc()).accessType(p.getAccessType())
                .build();
    }

    private PermissionResponse toResponseWithGrant(Permission p, Set<Permission> rolePerms) {
        return PermissionResponse.builder()
                .permUuid(p.getUuid()).name(p.getName())
                .permDesc(p.getPermDesc()).accessType(p.getAccessType())
                .granted(rolePerms.contains(p))
                .build();
    }
}
