package com.bloom.bloomschool.users.controller;

import com.bloom.bloomschool.users.dto.UserRequestDTO;
import com.bloom.bloomschool.users.dto.UserResponseDTO;
import com.bloom.bloomschool.users.service.UserService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

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

    //Assign permission to a user
    @PostMapping("/{userUuid}/permissions/{permissionUuid}")
    public ResponseEntity<?> assignPermissionToUser(@PathVariable UUID userUuid, @PathVariable UUID permissionUuid, @RequestParam String overrideType){
        userService.assignPermissionToUser(userUuid,permissionUuid,overrideType);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    //Delete permission
    @DeleteMapping("/{userUuid}/permissions/{permissionUuid}")
    public ResponseEntity<?> removePermissionFromUser(@PathVariable UUID userUuid, @PathVariable UUID permissionUuid) {
        userService.removePermissionFromUser(userUuid, permissionUuid);
        return ResponseEntity.noContent().build();
    }

    // Delete User
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID uuid){
        userService.deleteUser(uuid);
        return ResponseEntity.noContent().build();
    }
}