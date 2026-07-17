package com.bloom.bloomschool.auth.dto.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateUserRequest {
    @NotBlank private String userName;
    @NotBlank private String firstName;
    private String otherNames;
    @Email @NotBlank private String email;
    private String phoneNumber;
    private String profileRef;   // UUID of linked Staff / Student entity
    private Set<UUID> roleUuids;
}
