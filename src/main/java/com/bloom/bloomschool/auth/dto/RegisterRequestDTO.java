package com.bloom.bloomschool.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class RegisterRequestDTO {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Other names are required")
    private String otherNames;

    @NotBlank(message = "Phone is Required")
    private String phoneNumber;
}