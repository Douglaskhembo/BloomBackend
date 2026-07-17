package com.bloom.bloomschool.auth.dto.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ModuleRequest {
    @NotBlank private String moduleName;
}
