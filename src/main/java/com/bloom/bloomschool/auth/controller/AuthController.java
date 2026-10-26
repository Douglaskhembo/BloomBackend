package com.bloom.bloomschool.auth.controller;

import com.bloom.bloomschool.auth.dto.LoginResponseDTO;
import com.bloom.bloomschool.auth.dto.LoginRequestDTO;
import com.bloom.bloomschool.auth.dto.RegisterRequestDTO;
import com.bloom.bloomschool.auth.service.JwtService;
import com.bloom.bloomschool.users.entity.User;
import com.bloom.bloomschool.users.repository.UserRepository;
import com.bloom.bloomschool.users.service.UserService;
import com.bloom.bloomschool.student.exceptions.BadRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new BadRequestException("Account is deactivated. Contact your administrator.");
        }

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new BadRequestException("Account is locked due to failed login attempts.");
        }

        return ResponseEntity.ok(buildAuthResponse(user));
    }

    private LoginResponseDTO buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String rolesString = user.getRoles() != null
                ? user.getRoles().stream().map(r -> r.getName()).collect(Collectors.joining(","))
                : "";

        String fullName = user.getUsername() + " " + user.getOtherNames();

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .uuid(user.getUuid())
                .username(user.getUsername())
                .fullName(fullName)
                .role(rolesString)
                .firstLogin(Boolean.TRUE.equals(user.getFirstLogin()))
                .build();
    }
}