package com.bloom.bloomschool.auth.service;

import com.bloom.bloomschool.auth.model.Permission;
import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.auth.model.UserPermission;
import com.bloom.bloomschool.auth.repo.UserPermissionRepository;
import com.bloom.bloomschool.auth.repo.UserRepository;
import com.bloom.bloomschool.common.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissionResolver {

    private final UserRepository userRepo;
    private final UserPermissionRepository userPermissionRepo;
    private final UserUtils userUtils;

    public boolean currentUserHas(String permissionName) {
        String username = userUtils.getCurrentUser();
        if (username == null) return false;
        User user = userRepo.findByUserNameWithRoles(username).orElse(null);
        if (user == null) return false;
        return effectivePermissionNames(user).contains(permissionName);
    }

    public void requirePermission(String permissionName) {
        if (!currentUserHas(permissionName))
            throw new AccessDeniedException("This action requires the '" + permissionName + "' permission");
    }


    public boolean anyOtherUserHasPermission(String permissionName, String excludingUsername) {
        return userRepo.findAllWithRoles().stream()
                .filter(u -> !u.getUserName().equals(excludingUsername))
                .anyMatch(u -> effectivePermissionNames(u).contains(permissionName));
    }

    public Set<String> effectivePermissionNames(User user) {
        Set<String> names = new HashSet<>();
        user.getRoles().forEach(role -> role.getPermissions().forEach(p -> names.add(p.getName())));

        for (UserPermission override : userPermissionRepo.findByUserId(user.getId())) {
            Permission permission = override.getPermission();
            if (permission == null) continue;
            if ("GRANT".equals(override.getOverrideType())) names.add(permission.getName());
            else if ("REVOKE".equals(override.getOverrideType())) names.remove(permission.getName());
        }
        return names;
    }
}
