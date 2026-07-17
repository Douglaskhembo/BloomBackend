package com.bloom.bloomschool.auth.dto.Requests;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
    private String confirmPassword;
}
