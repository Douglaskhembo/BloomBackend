package com.bloom.bloomschool.users.dto;

import lombok.*;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModulesResponseDTO {
    private UUID uuid;
    private String name;

}
