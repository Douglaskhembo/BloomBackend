package com.bloom.bloomschool.auth.controller;

import com.bloom.bloomschool.auth.dto.Requests.ChangePasswordRequest;
import com.bloom.bloomschool.auth.dto.Requests.LoginRequest;
import com.bloom.bloomschool.auth.dto.Requests.ResetPasswordRequest;
import com.bloom.bloomschool.auth.service.AuthService;
import com.bloom.bloomschool.auth.utils.ApiResponse;
import com.bloom.bloomschool.auth.utils.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GenericResponse genericResponse;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody LoginRequest request) {
        return genericResponse.response(authService.login(request), HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@RequestBody ResetPasswordRequest request,
                                                             HttpServletRequest httpRequest) {
        authService.resetPassword(request, httpRequest);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @PostMapping("/admin/reset-password/{userUuid}")
    public ResponseEntity<ApiResponse<Object>> adminResetPassword(@PathVariable UUID userUuid) {
        authService.adminResetPassword(userUuid);
        return genericResponse.response(null, HttpStatus.OK);
    }

    @PostMapping("/toggle-2fa")
    public ResponseEntity<ApiResponse<Object>> toggle2FA() {
        return genericResponse.response(authService.toggle2FA(), HttpStatus.OK);
    }
}
