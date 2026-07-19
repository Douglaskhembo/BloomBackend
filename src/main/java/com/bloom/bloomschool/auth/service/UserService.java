package com.bloom.bloomschool.auth.service;

import com.bloom.bloomschool.auth.dto.PermissionsBean;
import com.bloom.bloomschool.auth.dto.Requests.CreateUserRequest;
import com.bloom.bloomschool.auth.dto.Requests.OnboardStaffRequest;
import com.bloom.bloomschool.auth.dto.Responses.OnboardStaffResponse;
import com.bloom.bloomschool.auth.dto.Responses.ParentLinkResult;
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
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final StaffRepository staffRepository;
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

    /**
     * Creates a login for an existing Staff member ("elect from staff") rather than
     * accepting free-typed personal details — name/email/phone are derived from the
     * Staff record itself, and profileRef links the account back to it.
     */
    @Transactional
    public OnboardStaffResponse onboardStaff(OnboardStaffRequest request) {
        Staff staff = staffRepository.findByUuid(request.getStaffUuid())
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        String staffRef = staff.getUuid().toString();
        if (userRepository.existsByProfileRef(staffRef))
            throw new EntityExistsException("A user account already exists for this staff member");
        if (staff.getEmail() != null && userRepository.existsByEmail(staff.getEmail()))
            throw new EntityExistsException("A user account already exists for this staff member's email");
        if (userRepository.existsByUserName(request.getUserName()))
            throw new IllegalArgumentException("Username already taken");

        Set<Role> roles = resolveRoles(request.getRoleUuids());
        String tempPassword = UUID.randomUUID().toString().substring(0, 10);

        User saved = userRepository.save(User.builder()
                .userName(request.getUserName())
                .firstName(staff.getFirstName())
                .otherNames(staff.getLastName())
                .email(staff.getEmail())
                .phoneNumber(staff.getPhone())
                .profileRef(staffRef)
                .password(passwordEncoder.encode(tempPassword))
                .active(false).firstLogin(true).roles(roles)
                .enable2FA(request.isEnable2FA())
                .passwordExpiry(LocalDateTime.now().plusHours(24))
                .build());

        seedInheritedPermissions(saved, roles);
        return OnboardStaffResponse.builder()
                .user(toResponse(saved))
                .temporaryPassword(tempPassword)
                .build();
    }

    /**
     * Links (or creates) a PARENT-role login for a student's guardian at admission-enrollment
     * time. Matches an existing PARENT-role user by email/phone first — so siblings admitted
     * later reuse the same parent account — and only creates a new one if no match is found.
     */
    @Transactional
    public ParentLinkResult onboardParent(String parentName, String parentEmail, String parentPhone) {
        String email = blankToNull(parentEmail);
        String phone = blankToNull(parentPhone);
        if (email == null && phone == null) {
            return ParentLinkResult.builder().linked(false).build();
        }

        Optional<User> existing = userRepository.findParentByEmailOrPhone(email, phone);
        if (existing.isPresent()) {
            return ParentLinkResult.builder().linked(true).user(existing.get()).newlyCreated(false).build();
        }

        Role parentRole = roleRepository.findByName("PARENT")
                .orElseThrow(() -> new IllegalStateException("PARENT role not found — ensure role seeding ran"));

        String[] nameParts = splitName(parentName);
        String tempPassword = UUID.randomUUID().toString().substring(0, 10);

        User saved = userRepository.save(User.builder()
                .userName(generateParentUserName(email, phone, parentName))
                .firstName(nameParts[0])
                .otherNames(nameParts[1])
                .email(email)
                .phoneNumber(phone)
                .password(passwordEncoder.encode(tempPassword))
                .active(false).firstLogin(true)
                .roles(new HashSet<>(Set.of(parentRole)))
                .passwordExpiry(LocalDateTime.now().plusHours(24))
                .build());

        seedInheritedPermissions(saved, Set.of(parentRole));
        return ParentLinkResult.builder().linked(true).user(saved).newlyCreated(true).temporaryPassword(tempPassword).build();
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

    private String generateParentUserName(String email, String phone, String parentName) {
        String base;
        if (email != null) base = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
        else if (parentName != null && !parentName.isBlank()) base = parentName.trim().toLowerCase().replaceAll("[^a-z0-9]+", "");
        else base = "parent" + (phone != null ? phone.replaceAll("\\D", "") : "");
        if (base.isBlank()) base = "parent";

        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUserName(candidate)) {
            candidate = base + suffix++;
        }
        return candidate;
    }

    private String[] splitName(String fullName) {
        if (fullName == null || fullName.isBlank()) return new String[]{"Parent", "Guardian"};
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length == 2 ? parts : new String[]{parts[0], ""};
    }

    private String blankToNull(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }

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
