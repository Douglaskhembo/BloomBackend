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

/**
 * Resolves a user's effective permission set: Role.permissions unioned with UserPermission GRANT
 * overrides, minus REVOKE overrides. This data model already existed (Role/Permission/UserPermission)
 * but was never read at request time — grant/revoke in Roles & Permissions was informational only.
 * This is the first real backend enforcement point, deliberately scoped to the endpoints that call it.
 */
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

    /**
     * True if some user other than {@code excludingUsername} currently holds the given permission.
     * Used to allow a lone holder of an approval permission to act on their own submission — mirrors
     * the payroll workflow's "sole approver" exception (see PayrollService.decideStep) — since strict
     * self-approval blocking would make the action un-actionable in a single-admin school.
     */
    public boolean anyOtherUserHasPermission(String permissionName, String excludingUsername) {
        return userRepo.findAllWithRoles().stream()
                .filter(u -> !u.getUserName().equals(excludingUsername))
                .anyMatch(u -> effectivePermissionNames(u).contains(permissionName));
    }

    /** A user's full effective permission set: every Role.permissions they hold, plus individual
     *  UserPermission GRANT overrides, minus REVOKE overrides. This is the single source of truth
     *  for "what can this user do" — anywhere permissions are reported to the user (e.g. the login
     *  response) must call this exact method rather than re-deriving role permissions alone, or it
     *  silently drops individual per-user grants/revokes. See AuthService.extractPermissions. */
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
