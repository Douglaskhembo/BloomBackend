package com.bloom.bloomschool.auth.service;

import com.bloom.bloomschool.auth.dto.PermissionsBean;
import com.bloom.bloomschool.auth.dto.Requests.CreateUserRequest;
import com.bloom.bloomschool.auth.dto.Responses.UserResponse;
import com.bloom.bloomschool.auth.dto.UserRolesDTO;
import com.bloom.bloomschool.auth.model.Permission;
import com.bloom.bloomschool.auth.model.Role;
import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.auth.model.UserPermission;
import com.bloom.bloomschool.auth.repo.PermissionRepository;
import com.bloom.bloomschool.auth.repo.RoleRepository;
import com.bloom.bloomschool.auth.repo.UserPermissionRepository;
import com.bloom.bloomschool.auth.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse getUserByUuid(UUID uuid) {
        return toResponse(userRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUserName(request.getUserName()))
            throw new IllegalArgumentException("Username already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email already registered");

        Set<Role> roles = resolveRoles(request.getRoleUuids());
        String tempPassword = UUID.randomUUID().toString().substring(0, 10);

        User saved = userRepository.save(User.builder()
                .userName(request.getUserName())
                .firstName(request.getFirstName())
                .otherNames(request.getOtherNames())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .profileRef(request.getProfileRef())
                .password(passwordEncoder.encode(tempPassword))
                .active(false).firstLogin(true).roles(roles)
                .build());

        seedInheritedPermissions(saved, roles);
        return toResponse(saved);
    }

    @Transactional
    public UserResponse updateUser(UUID uuid, CreateUserRequest request) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setOtherNames(request.getOtherNames());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setProfileRef(request.getProfileRef());
        if (request.getRoleUuids() != null) user.setRoles(resolveRoles(request.getRoleUuids()));
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void toggleUserStatus(UUID uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user.getUserName().equals(authService.getLoggedInUserName()))
            throw new IllegalArgumentException("You cannot enable/disable your own account.");
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID uuid) {
        userRepository.delete(userRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    // ── Role assignment ──────────────────────────────────────────────────────

    public List<Role> getAssignedRoles(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new ArrayList<>(user.getRoles());
    }

    @Transactional
    public void assignRoles(UserRolesDTO dto) {
        if (dto.getUserUuid() == null) throw new IllegalArgumentException("User UUID required");
        if (dto.getRoleUuids() == null || dto.getRoleUuids().isEmpty())
            throw new IllegalArgumentException("Role UUIDs required");

        User user = userRepository.findByUuid(dto.getUserUuid())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<Role> toAdd = new HashSet<>();
        for (UUID roleUuid : dto.getRoleUuids()) {
            toAdd.add(roleRepository.findByUuid(roleUuid)
                    .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleUuid)));
        }
        user.getRoles().addAll(toAdd);
        userRepository.save(user);
        seedInheritedPermissions(user, toAdd);
    }

    @Transactional
    public void unassignRoles(UserRolesDTO dto) {
        if (dto.getUserUuid() == null) throw new IllegalArgumentException("User UUID required");
        if (dto.getRoleUuids() == null || dto.getRoleUuids().isEmpty())
            throw new IllegalArgumentException("Role UUIDs required");

        User user = userRepository.findByUuid(dto.getUserUuid())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        for (UUID roleUuid : dto.getRoleUuids()) {
            Role role = roleRepository.findByUuid(roleUuid)
                    .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleUuid));
            user.getRoles().remove(role);
            for (Permission perm : role.getPermissions())
                userPermissionRepository.deleteByUserIdAndPermissionId(user.getId(), perm.getId());
        }
        userRepository.save(user);
    }

    // ── User-level permission overrides ──────────────────────────────────────

    @Transactional
    public void grantUserPermission(PermissionsBean bean) {
        if (bean.getUserUuid() == null) throw new IllegalArgumentException("User UUID required");
        if (bean.getPermissionUuids() == null || bean.getPermissionUuids().isEmpty())
            throw new IllegalArgumentException("Permission UUIDs required");

        User user = userRepository.findByUuid(bean.getUserUuid())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        for (UUID permUuid : bean.getPermissionUuids()) {
            Permission perm = permissionRepository.findByUuid(permUuid)
                    .orElseThrow(() -> new EntityNotFoundException("Permission not found: " + permUuid));
            UserPermission up = userPermissionRepository.findByUserIdAndPermissionId(user.getId(), perm.getId());
            if (up != null) {
                if ("GRANT".equalsIgnoreCase(up.getOverrideType())) continue;
                up.setOverrideType("GRANT");
            } else {
                up = UserPermission.builder().user(user).permission(perm).overrideType("GRANT").build();
            }
            userPermissionRepository.save(up);
        }
    }

    @Transactional
    public void revokeUserPermission(PermissionsBean bean) {
        if (bean.getUserUuid() == null) throw new IllegalArgumentException("User UUID required");
        if (bean.getPermissionUuid() == null) throw new IllegalArgumentException("Permission UUID required");

        User user = userRepository.findByUuid(bean.getUserUuid())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Permission perm = permissionRepository.findByUuid(bean.getPermissionUuid())
                .orElseThrow(() -> new EntityNotFoundException("Permission not found"));

        UserPermission up = userPermissionRepository.findByUserIdAndPermissionId(user.getId(), perm.getId());
        if (up != null) {
            if ("REVOKE".equalsIgnoreCase(up.getOverrideType()))
                throw new IllegalArgumentException("Permission already revoked");
            up.setOverrideType("REVOKE");
        } else {
            up = UserPermission.builder().user(user).permission(perm).overrideType("REVOKE").build();
        }
        userPermissionRepository.save(up);
    }

    public List<PermissionsBean> getUserEffectivePermissions(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userPermissionRepository.findUserPermissions(user.getId()).stream()
                .map(obj -> PermissionsBean.builder()
                        .permissionUuid(obj[0] != null ? UUID.fromString(obj[0].toString()) : null)
                        .name((String) obj[1])
                        .permDesc((String) obj[2])
                        .accessType((String) obj[3])
                        .moduleUuid(obj[4] != null ? UUID.fromString(obj[4].toString()) : null)
                        .overrideType(obj[5] == null ? "INHERITED" : (String) obj[5])
                        .build())
                .toList();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void seedInheritedPermissions(User user, Set<Role> roles) {
        for (Role role : roles) {
            for (Permission perm : role.getPermissions()) {
                if (userPermissionRepository.findByUserIdAndPermissionId(user.getId(), perm.getId()) == null) {
                    userPermissionRepository.save(UserPermission.builder()
                            .user(user).permission(perm).overrideType(null).build());
                }
            }
        }
    }

    private Set<Role> resolveRoles(Set<UUID> roleUuids) {
        if (roleUuids == null || roleUuids.isEmpty()) return new HashSet<>();
        return roleUuids.stream()
                .map(uid -> roleRepository.findByUuid(uid)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + uid)))
                .collect(Collectors.toSet());
    }

    private UserResponse toResponse(User u) {
        Role primary = (u.getRoles() != null && !u.getRoles().isEmpty())
                ? u.getRoles().iterator().next() : null;
        return UserResponse.builder()
                .userUuid(u.getUuid())
                .userName(u.getUserName())
                .firstName(u.getFirstName())
                .otherNames(u.getOtherNames())
                .email(u.getEmail())
                .phoneNumber(u.getPhoneNumber())
                .active(u.isActive())
                .firstLogin(u.isFirstLogin())
                .enable2FA(u.isEnable2FA())
                .profileRef(u.getProfileRef())
                .roles(u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .roleName(primary != null ? primary.getName() : null)
                .roleUuid(primary != null ? primary.getUuid() : null)
                .build();
    }
}
