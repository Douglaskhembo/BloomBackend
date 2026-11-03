package com.bloom.bloomschool.users.service;

import com.bloom.bloomschool.auth.dto.PermissionsBean;
import com.bloom.bloomschool.staff.entity.StaffEntity;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.users.dto.*;
import com.bloom.bloomschool.users.entity.*;
import com.bloom.bloomschool.users.repository.*;
import com.bloom.bloomschool.student.exceptions.BadRequestException;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityExistsException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserPermissionsRepository userPermRepo;
    private final PermissionsRepository permRepo;
    private final StaffRepository staffRepo;
    private final ModulesRepository modulesRepo;

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
            throw new RuntimeException("Username Already Taken!");
        }
        if(userRepo.existsByEmail(req.getEmail())){
            throw new RuntimeException("Email Already Taken!");
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
                .password(passwordEncoder.encode(tempPassword))
                .passwordExpiry(LocalDateTime.now().plusHours(24))
                .roles(roles)
                .build());
        seedInheritedPermissions(user,roles);

    }
    private Set<Roles> roleHelper(Set<UUID> uuids){
        if (uuids == null || uuids.isEmpty()) return new HashSet<>();
        return uuids.stream()
                .map(u -> roleRepo.findByUuid(u)
                        .orElseThrow(() -> new ResourceNotFoundException("Role does not exist")))
                .collect(Collectors.toSet());
    }

    private void seedInheritedPermissions(User user, Set<Roles> roles){
        for (Roles role : roles){
            if (role.getModules() != null ){
                for (Modules module :role.getModules()){
                    if (module.getPermissions() != null){
                        for(Permissions permission : module.getPermissions()){
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
            }

        }

    }

    @Transactional
    public void onboardStaff(UserRequestDTO req) {

        StaffEntity staff = staffRepo.findByUuid(req.getStaffUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found!"));

        String staffRef = req.getStaffUuid().toString();

        if (userRepo.existsByProfileRef(staffRef)){
            throw new EntityExistsException("User account Already exists for this staff");
        }

        if (userRepo.existsByUsername(req.getUsername())) {
            throw new BadRequestException("Username Already Taken!");
        }

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new BadRequestException("User account already exists for this staff's member email!");
        }

        Set<Roles> roles = roleHelper(req.getRoleUuids());
        String tempPassword = UUID.randomUUID().toString().substring(0, 10);

        User user = userRepo.save(User.builder()
                .username(req.getUsername())
                .otherNames(req.getOtherNames())
                .firstName(req.getFirstName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .staff(staff)
                .profileRef(staffRef)
                .active(false)
                .firstLogin(true)
                .enable2FA(req.getEnable2FA())
                .password(passwordEncoder.encode(tempPassword))
                .passwordExpiry(LocalDateTime.now().plusHours(24))
                .roles(roles)
                .build());

        seedInheritedPermissions(user, roles);
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

        if (request.getStaffUuid() != null) {
            StaffEntity staff = staffRepo.findByUuid(request.getStaffUuid())
                    .orElseThrow(()-> new ResourceNotFoundException("Staff not found!"));
            String staffRef = request.getStaffUuid().toString();

            Optional<User> existingUserWithStaff = userRepo.findByProfileRef(staffRef);
            if (existingUserWithStaff.isPresent() && !existingUserWithStaff.get().getUserId().equals(u.getUserId())) {
                throw new BadRequestException("User account already exists for this staff member");
            }
            u.setStaff(staff);
            u.setProfileRef(staffRef);
        }

        if (request.getRoleUuids() != null){
            Set<Roles> newRoles = roleHelper(request.getRoleUuids());
            u.setRoles(newRoles);
        }

        userRepo.save(u);
    }

    // Toggle Status
    @Transactional
    public void toggleUserActive(UUID uuid) {
        User u = userRepo.findByUuid(uuid)
                .orElseThrow(()-> new BadRequestException("Entity not found"));
        u.setActive(!u.isActive());

        userRepo.save(u);
    }

    //Toggle 2FA
    @Transactional
    public void toggle2FA(UUID uuid){

        User u = userRepo.findByUuid(uuid)
                .orElseThrow(()-> new BadRequestException("Entity not found"));

        u.setEnable2FA(!u.isEnable2FA());
        userRepo.save(u);
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
                .active(u.isActive())
                .firstLogin(u.isFirstLogin())
                .accountLocked(u.isAccountLocked())
                .failedLoginAttempts(u.getFailedLoginAttempts())
                .enable2FA(u.isEnable2FA())
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

    public List<PermissionsBean>  getEffectiveUserPermissions(UUID uuid) {
        User user = userRepo.findByUuid(uuid)
                .orElseThrow(()-> new ResourceNotFoundException("User does not exist"));
        List<PermissionsBean> permList = new ArrayList<>();

        List<Object[]> permObj =  userPermRepo.findByUserPermissions(user.getUserId());
        for(Object[] obj: permObj){
            PermissionsBean pBean = new PermissionsBean();
            pBean.setPermissionUuid(obj[0] != null ? (UUID) obj[0] : null);
            pBean.setName((String) obj[1]);
            pBean.setPermissionDescription((String) obj[2]);
            pBean.setAccessType((String) obj[3]);
            pBean.setModuleUuid(obj[4] != null ? (UUID) obj[4] : null);
            pBean.setOverrideType(obj[5] == null ? "INHERITED" :(String) obj[5]);
            permList.add(pBean);
        }
        return permList;
    }

    // Grant permission
    @Transactional
    public void grantUserPermission(PermissionsBean permBean) {
        if(permBean.getUserUuid() == null) throw new IllegalArgumentException("User uuid is required!");
        if (permBean.getPermissionUuids() == null) throw new IllegalArgumentException("Permission  uuid is required!");
        User user = userRepo.findByUuid(permBean.getUserUuid())
                .orElseThrow(()-> new ResourceNotFoundException("User does not exist"));
        for(UUID permUuid :permBean.getPermissionUuids()){
            Permissions perm = permRepo.findByUuid(permUuid)
                    .orElseThrow(()-> new ResourceNotFoundException("Permission not found: "+ permUuid));
            UserPermissions uPerm = userPermRepo.findByUserIdAndPermissionId(user.getUserId(), perm.getPermissionsId());
            if(uPerm != null){
                if ("GRANT".equalsIgnoreCase(uPerm.getOverrideType())) continue;
                uPerm.setOverrideType("GRANT");
            } else{
                uPerm = UserPermissions.builder()
                        .user(user)
                        .permission(perm)
                        .overrideType("GRANT")
                        .build();
            }
            userPermRepo.save(uPerm);
        }
    }

    @Transactional
    public void revokeUserPermission(PermissionsBean permBean) {
        if (permBean.getUserUuid() == null) throw new IllegalArgumentException("User uuid is required!");
        if (permBean.getPermissionUuid() == null) throw new IllegalArgumentException("Permission uuid is required!");

        User user = userRepo.findByUuid(permBean.getUserUuid())
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist!"));

        Permissions perm = permRepo.findByUuid(permBean.getPermissionUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Permission does not exist!"));

        UserPermissions uPerm = userPermRepo.findByUserIdAndPermissionId(user.getUserId(), perm.getPermissionsId());

        if (uPerm != null) {
            uPerm.setOverrideType("REVOKE");
        } else {
            uPerm = UserPermissions.builder()
                    .user(user)
                    .permission(perm)
                    .overrideType("REVOKE")
                    .build();
        }

        userPermRepo.save(uPerm);
    }

    @Transactional
    public void assignRolesToUser(UserRolesRequestDTO req){
        if (req.getRoleUuids() == null || req.getRoleUuids().isEmpty()){
            throw new IllegalArgumentException("At least role uuid must be provided");
        }
        if (req.getUserUuid() == null){
            throw new IllegalArgumentException("At least a user uuid must be supplied");
        }

        User user = userRepo.findByUuid(req.getUserUuid())
                .orElseThrow(()-> new ResourceNotFoundException("User does not exist!"));

        Set<Roles> incomingRoles = roleHelper(req.getRoleUuids());

        Set<Roles> newRoles = incomingRoles.stream()
                .filter(role -> !user.getRoles().contains(role))
                .collect(Collectors.toSet());

        if (!newRoles.isEmpty()){
            user.getRoles().addAll(newRoles);
        }
        userRepo.save(user);

        seedInheritedPermissions(user, incomingRoles);
    }

    @Transactional
    public void unAssignRoleFromUser(UserRolesRequestDTO req){
        if (req.getRoleUuids() == null || req.getRoleUuids().isEmpty()) {
            throw new IllegalArgumentException("At least a role uuid must be supplied");
        }
        if (req.getUserUuid() == null){
            throw new IllegalArgumentException("At least a user must be provided");
        }

        User user = userRepo.findByUuid(req.getUserUuid())
                .orElseThrow(() -> new ResourceNotFoundException("User does not Exist"));

        Set<Roles> rolesToUnAssign = roleHelper(req.getRoleUuids());

        Set<UUID> unassignedRoleUuids = rolesToUnAssign.stream()
                .map(Roles::getUuid)
                .collect(Collectors.toSet());

        for (Roles role : rolesToUnAssign){
            if (user.getRoles().contains(role)){
                user.getRoles().remove(role);

                if (role.getModules() != null) {
                    for (Modules module : role.getModules()) {
                        if (module.getPermissions() != null) {
                            for (Permissions permission : module.getPermissions()){

                                boolean stillGrantedByAnotherRole = user.getRoles().stream()
                                        .filter(r -> !unassignedRoleUuids.contains(r.getUuid()))
                                        .flatMap(r -> r.getModules() != null ? r.getModules().stream() : Stream.<Modules>empty())
                                        .flatMap(m -> m.getPermissions() != null ? m.getPermissions().stream() : Stream.<Permissions>empty())
                                        .anyMatch(p -> p.getPermissionsId().equals(permission.getPermissionsId()));

                                if (!stillGrantedByAnotherRole) {
                                    UserPermissions uPerm = userPermRepo.findByUserIdAndPermissionId(
                                            user.getUserId(),
                                            permission.getPermissionsId()
                                    );

                                    if (uPerm != null && uPerm.getOverrideType() == null) {
                                        userPermRepo.delete(uPerm);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        userRepo.save(user);
    }

    @Transactional
    public void mapRolesToModule(ModuleRoleRequestDTO req) {
        if (req.getRoleUuid() == null){
            throw new IllegalArgumentException("At least a role uuid must be provided");
        }
        if (req.getModuleUuids() == null || req.getModuleUuids().isEmpty()){
            throw new IllegalArgumentException("At least module uuid must be provided");
        }
        Roles roles = roleRepo.findByUuid(req.getRoleUuid())
                .orElseThrow(()-> new ResourceNotFoundException("Role does not exist"));

        for (UUID moduleUuid :req.getModuleUuids()){
            Modules module = modulesRepo.findByUuid(moduleUuid)
                    .orElseThrow(()-> new ResourceNotFoundException("Module does not Exist"));
            roles.getModules().add(module);
        }
        roleRepo.save(roles);
    }
}