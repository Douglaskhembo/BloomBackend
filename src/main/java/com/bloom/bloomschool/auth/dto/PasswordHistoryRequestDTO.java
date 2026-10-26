package com.bloom.bloomschool.auth.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class PasswordHistoryRequestDTO {
    private String passwordHash;
    private LocalDateTime createdAt;
}
