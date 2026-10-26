package com.bloom.bloomschool.auth.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class PasswordHistoryResponseDTO {
    private UUID uuid;
    private String passwordHash;
    private LocalDateTime createdAt;
}
