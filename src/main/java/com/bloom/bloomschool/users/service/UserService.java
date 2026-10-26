package com.bloom.bloomschool.users.service;

import com.bloom.bloomschool.users.entity.Permissions;
import com.bloom.bloomschool.users.entity.Roles;
import com.bloom.bloomschool.users.entity.User;
import com.bloom.bloomschool.users.dto.UserResponseDTO;
import com.bloom.bloomschool.users.dto.UserRequestDTO;
import com.bloom.bloomschool.users.dto.RolesResponseDTO;
import com.bloom.bloomschool.users.entity.UserPermissions;
import com.bloom.bloomschool.users.repository.PermissionsRepository;
import com.bloom.bloomschool.users.repository.RoleRepository;
import com.bloom.bloomschool.users.repository.UserPermissionsRepository;
import com.bloom.bloomschool.users.repository.UserRepository;
import com.bloom.bloomschool.auth.dto.RegisterRequestDTO;
import com.bloom.bloomschool.student.exceptions.BadRequestException;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserPermissionsRepository userPermRepo;
    private final PermissionsRepository permRepo;

    private User findEntity(UUID uuid){
        return userRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found!!"));
    }

    private User findEntity(String email){
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found!!"));
    }

    // Get All Users
    public List<UserResponseDTO> getUsers(){
        return userRepo.findAll().stream().map(this::toResponse).toList();
    }

    // Get User By UUID
    public UserResponseDTO getUserByUuid(UUID uuid) {
        return toResponse(findEntity(uuid));
    }

    // Get User By email
    public UserResponseDTO getUserByEmail(String email) {
        return toResponse(findEntity(email));
    }

    // Create User
    @Transactional
    public void createUser(UserRequestDTO req){
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("User Already Exists!!");
        }
        if(userRepo.existsByEmail(req.getEmail())){
            throw new RuntimeException("Email already registered");
        }
        Set<Roles> roles = roleHelper(req.getRoleUuids());

        String tempPassword = UUID.randomUUID().toString().substring(0,10);

        User user = userRepo.save(User.builder()
                .username(req.getUsername())
                .otherNames(req.getOtherNames())
                .firstName(req.getFirstName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .active(false)
                .firstLogin(true)
                .enable2FA(req.getEnable2FA() != null ? req.getEnable2FA() : false)
                .password(tempPassword)
                .passwordExpiry(LocalDateTime.now().plusHours(24))
                .roles(roles)
                .build());
        seedInheritedPermissions(user,roles);

    }
    private Set<Roles> roleHelper(Set<UUID> uuids){
        if (uuids == null || uuids.isEmpty()) return new HashSet<>();
        return uuids.stream()
                .map(u -> roleRepo.findByUuid(u)
                        .orElseThrow(() -> new ResourceNotFoundException("uuid does not exist")))
                .collect(Collectors.toSet());
    }

    private void seedInheritedPermissions(User user, Set<Roles> roles){
        for (Roles role : roles){
            for(Permissions permission : role.getPermission()){
                if(userPermRepo.findByUserIdAndPermissionId(user.getUserId(), permission.getPermissionsId()) == null){
                    userPermRepo.save(UserPermissions.builder()
                                    .user(user)
                                    .permission(permission)
                                    .overrideType(null)
                            .build());
                }
            }
        }

    }

    @Transactional
    public void registerUser(RegisterRequestDTO request) {
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .otherNames(request.getOtherNames())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .accountLocked(false)
                .firstLogin(false)
                .enable2FA(false)
                .build();

        userRepo.save(user);
    }

    // Update User
    @Transactional
    public void updateUser(UUID uuid, UserRequestDTO request){
        User u = findEntity(uuid);

        if (request.getUsername() != null && !request.getUsername().equals(u.getUsername())){
            if(userRepo.existsByUsername(request.getUsername())){
                throw new BadRequestException("Username already exists!");
            }
            u.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(u.getEmail())){
            if (userRepo.existsByEmail(request.getEmail())){
                throw new BadRequestException("Email already Registered!");
            }
            u.setEmail(request.getEmail());
        }
        if (request.getOtherNames() != null) u.setOtherNames(request.getOtherNames());
        if (request.getFirstName() != null) u.setFirstName(request.getFirstName());
        if (request.getPhoneNumber() != null) u.setPhoneNumber(request.getPhoneNumber());

        if (request.getRoleUuids() != null){
            Set<Roles> newRoles = roleHelper(request.getRoleUuids());
            u.setRoles(newRoles);
            seedInheritedPermissions(u, newRoles);
        }

        userRepo.save(u);
    }

    // Toggle Status
    @Transactional
    public void toggleUserActive(UUID uuid) {
        User u = findEntity(uuid);
        boolean currentActive = Boolean.TRUE.equals(u.getActive());
        u.setActive(!currentActive);
        userRepo.save(u);
    }

    //Toggle 2FA
    @Transactional
    public void toggle2FA(UUID uuid){
        User u = findEntity(uuid);
        boolean enable2FA = Boolean.TRUE.equals(u.getEnable2FA());
        u.setEnable2FA(!enable2FA);
        userRepo.save(u);
    }

    //Assign or update permission to a user
    @Transactional
    public void assignPermissionToUser(UUID userUuid, UUID permissionUuid, String overrideType){
        User u = findEntity(userUuid);

        Permissions p = permRepo.findByUuid(permissionUuid)
                .orElseThrow(()-> new ResourceNotFoundException("Permission Does Not Exist"));

        if(!"ALLOW".equalsIgnoreCase(overrideType) && !"DENY".equalsIgnoreCase(overrideType)){
            throw new BadRequestException("OverrideType must be ALLOW or DENY");
        }

        UserPermissions up = userPermRepo.findByUserAndPermission(u, p)
                .orElse(UserPermissions.builder()
                        .user(u)
                        .permission(p)
                        .build());
        up.setOverrideType(overrideType.toUpperCase());
        userPermRepo.save(up);

    }

    //Remove Permission from user
    @Transactional
    public void removePermissionFromUser(UUID userUuid, UUID permissionUuid){
        User u = findEntity(userUuid);

        Permissions p = permRepo.findByUuid(permissionUuid)
                .orElseThrow(()-> new ResourceNotFoundException("Permissions Do Not exist"));

        UserPermissions up = userPermRepo.findByUserAndPermission(u, p)
                .orElseThrow(()-> new ResourceNotFoundException("User Permission Not Found!"));

        userPermRepo.delete(up);

    }


    // Delete User
    @Transactional
    public void deleteUser(UUID uuid){
        User u = findEntity(uuid);
        userRepo.delete(u);
    }

    private UserResponseDTO toResponse(User u){
        return UserResponseDTO.builder()
                .uuid(u.getUuid())
                .username(u.getUsername())
                .firstName(u.getFirstName())
                .otherNames(u.getOtherNames())
                .email(u.getEmail())
                .phoneNumber(u.getPhoneNumber())
                .sessionToken(u.getSessionToken())
                .active(u.getActive())
                .firstLogin(u.getFirstLogin())
                .accountLocked(u.getAccountLocked())
                .failedLoginAttempts(u.getFailedLoginAttempts())
                .enable2FA(u.getEnable2FA())
                .passwordExpiry(u.getPasswordExpiry())
                .roles(mapRolesToDTO(u.getRoles()))
                .build();
    }

    private Set<RolesResponseDTO> mapRolesToDTO(Set<Roles> roles) {
        if (roles == null || roles.isEmpty()) {return new HashSet<>();}

        Set<RolesResponseDTO> roleResponseDTOs = new HashSet<>();
        for (Roles r : roles) {
            roleResponseDTOs.add(RolesResponseDTO.builder()
                    .uuid(r.getUuid())
                    .name(r.getName())
                    .build());
        }
        return roleResponseDTOs;
    }
}