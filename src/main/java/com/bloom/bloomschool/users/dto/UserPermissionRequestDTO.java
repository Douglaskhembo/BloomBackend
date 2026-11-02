package com.bloom.bloomschool.users.dto;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserPermissionRequestDTO {
    private String overrideType;
}
