package com.bloom.bloomschool.auth.dto.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleRequest {
    @NotBlank private String roleName;
}
