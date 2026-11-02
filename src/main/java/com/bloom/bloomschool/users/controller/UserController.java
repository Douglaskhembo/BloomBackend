package com.bloom.bloomschool.users.controller;

import com.bloom.bloomschool.auth.dto.PermissionsBean;
import com.bloom.bloomschool.auth.utils.ApiResponse;
import com.bloom.bloomschool.auth.utils.GenericResponse;
import com.bloom.bloomschool.users.dto.*;
import com.bloom.bloomschool.users.service.UserService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final GenericResponse genericResponse;

    // Get All Users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers(){

        return ResponseEntity.ok(userService.getUsers());
    }

    // Get User By UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponseDTO> getUserByUuid(@PathVariable UUID uuid){
        return ResponseEntity.ok(userService.getUserByUuid(uuid));
    }

    // Get User By Email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // Create User
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO req){
        userService.createUser(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //Onboard Staff
    @PostMapping("/onboard-staff")
    public ResponseEntity<?> onboardStaff(@RequestBody UserRequestDTO req){
        userService.onboardStaff(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Update User
    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateUser(@PathVariable UUID uuid, @RequestBody UserRequestDTO req){
        userService.updateUser(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // Toggle Status
    @PatchMapping("/toggleStatus/{uuid}")
    public ResponseEntity<?> toggleStatus(@PathVariable UUID uuid){
        userService.toggleUserActive(uuid);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    //Toggle 2FA
    @PatchMapping("/toggle2FA/{uuid}")
    public ResponseEntity<?> toggle2FA(@PathVariable UUID uuid){
        userService.toggle2FA(uuid);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @GetMapping("/{uuid}/effective-permissions")
    public ResponseEntity<ApiResponse<Object>> effectivePermissions(@PathVariable UUID uuid){

        return genericResponse.response(userService.getEffectiveUserPermissions(uuid),HttpStatus.OK);
    }
    @PostMapping("/grant-permission")
    public ResponseEntity<ApiResponse<Object>> grantPermission(@RequestBody PermissionsBean permBean){
        userService.grantUserPermission(permBean);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @DeleteMapping("/revoke-permission")
    public ResponseEntity<ApiResponse<Object>> revokePermission(@RequestBody PermissionsBean permBean){
        userService.revokeUserPermission(permBean);
        return genericResponse.response(null, HttpStatus.OK);
    }
    //Assign Role
    @PostMapping("/assign-role")
    public ResponseEntity<ApiResponse<Object>> assignRole(@RequestBody UserRolesRequestDTO req){
        userService.assignRolesToUser(req);
        return genericResponse.response(null, HttpStatus.OK);
    }

    //UnAssign Role
    @DeleteMapping("/unassign-role")
    public ResponseEntity<ApiResponse<Object>> unassignRole(@RequestBody UserRolesRequestDTO req){
        userService.unAssignRoleFromUser(req);
        return genericResponse.response(null, HttpStatus.OK);
    }

    //Assign Role to a module
    @PostMapping("/modules/assign-role")
    public ResponseEntity<ApiResponse<Object>> assignRoleToModule(@RequestBody ModuleRoleRequestDTO req){
        userService.mapRolesToModule(req);
        return genericResponse.response(null, HttpStatus.OK);
    }

    // Delete User
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID uuid){
        userService.deleteUser(uuid);
        return ResponseEntity.noContent().build();
    }
}