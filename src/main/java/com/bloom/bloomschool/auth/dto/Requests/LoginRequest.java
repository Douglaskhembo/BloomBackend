package com.bloom.bloomschool.auth.dto.Requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
