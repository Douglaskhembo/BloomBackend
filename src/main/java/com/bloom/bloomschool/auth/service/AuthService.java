package com.bloom.bloomschool.auth.service;

import com.bloom.bloomschool.auth.config.CustomUserDetails;
import com.bloom.bloomschool.auth.config.JwtService;
import com.bloom.bloomschool.auth.dto.Requests.ChangePasswordRequest;
import com.bloom.bloomschool.auth.dto.Requests.LoginRequest;
import com.bloom.bloomschool.auth.dto.Requests.ResetPasswordRequest;
import com.bloom.bloomschool.auth.dto.Responses.LoginResponse;
import com.bloom.bloomschool.auth.model.PasswordHistory;
import com.bloom.bloomschool.auth.model.Permission;
import com.bloom.bloomschool.auth.model.Role;
import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.auth.repo.PasswordHistoryRepository;
import com.bloom.bloomschool.auth.repo.RoleRepository;
import com.bloom.bloomschool.auth.repo.UserRepository;
import com.bloom.bloomschool.notiffication.service.MailService;
import com.bloom.bloomschool.school.service.SchoolService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SchoolService schoolService;
    private final PasswordHistoryRepository passwordHistoryRepository;

    private static final Map<String, String> REDIRECT = Map.of(
            "ADMIN",   "/admin",
            "TEACHER", "/teacher",
            "PARENT",  "/parent"
    );

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!user.isActive())
            throw new IllegalArgumentException("Account disabled. Please contact the administrator.");

        if (user.isAccountLocked())
            throw new IllegalArgumentException("Account locked. Please contact the administrator.");

        try {

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            // Successful login
            user.setFailedLoginAttempts(0);
            userRepository.save(user);

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            user = userDetails.getUser();

            if (user.getPasswordExpiry() != null &&
                    LocalDateTime.now().isAfter(user.getPasswordExpiry()))
                throw new IllegalArgumentException("Password expired. Please contact the administrator.");

            Set<String> permissions = extractPermissions(user);
            String token = issueSession(user);

            return buildLoginResponse(user, token, permissions);

        } catch (BadCredentialsException ex) {

            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= 3) {
                user.setAccountLocked(true);
            }

            userRepository.save(user);

            throw new IllegalArgumentException(
                    "Invalid username or password. Attempt " + attempts + " of 3."
            );
        }
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByUserName(getLoggedInUserName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new IllegalArgumentException("Incorrect current password");
        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new IllegalArgumentException("Passwords do not match");
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new IllegalArgumentException("New password must differ from the current one");
        savePassword(user, request.getNewPassword());

        try {
            mailService.sendPasswordChangedConfirmation(user.getEmail(), user.getFirstName());
        } catch (Exception e) {
            log.error("Failed to send password reset confirmation email to {}", user.getEmail(), e);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest) {
        String token = jwtService.extractToken(httpRequest);
        String userName = (token != null) ? jwtService.extractUsername(token) : getLoggedInUserName();
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new IllegalArgumentException("Passwords do not match");
        savePassword(user, request.getNewPassword());

        try {
            mailService.sendPasswordChangedConfirmation(user.getEmail(), user.getFirstName());
        } catch (Exception e) {
            log.error("Failed to send password reset confirmation email to {}", user.getEmail(), e);
        }

    }

    @Transactional
    public void adminResetPassword(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        String tempPassword = UUID.randomUUID().toString().substring(0, 10);
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setFirstLogin(true);
        user.setPasswordExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        try{
            mailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), schoolService.getSchoolInfo().getName(), tempPassword);
        }catch (Exception e){
            log.error("Failed to send admin reset confirmation email to {}", user.getEmail(), e);
        }
        log.info("Admin reset password for user: {}", user.getUserName());
    }

    @Transactional
    public boolean toggle2FA() {
        User user = userRepository.findByUserName(getLoggedInUserName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setEnable2FA(!user.isEnable2FA());
        userRepository.save(user);
        return user.isEnable2FA();
    }

    private String issueSession(User user) {
        user.setSessionToken(UUID.randomUUID().toString());
        userRepository.save(user);
        return jwtService.generateToken(user);
    }

    private Set<String> extractPermissions(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }

    private LoginResponse buildLoginResponse(User user, String token, Set<String> permissions) {
        Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        String primaryRole = roleNames.stream().findFirst().orElse("UNKNOWN");
        String redirectPath = REDIRECT.getOrDefault(primaryRole, "/admin");
        return new LoginResponse(
                user.getUuid(), token, user.getUserName(),
                user.getFirstName(), user.getOtherNames(),
                user.getEmail(), user.getPhoneNumber(),
                roleNames.toString(), permissions,
                user.isFirstLogin(), false, user.isEnable2FA(),
                user.getProfileRef(), redirectPath
        );
    }

    @Transactional
    protected void savePassword(User user, String newPassword) {

        if (user.isFirstLogin()) {
            user.setFirstLogin(false);
            user.setActive(true);
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException(
                    "You cannot reuse your current password.");
        }

        List<PasswordHistory> history =
                passwordHistoryRepository.findTop5ByUserOrderByChangedAtDesc(user);

        for (PasswordHistory item : history) {
            if (passwordEncoder.matches(newPassword, item.getPasswordHash())) {
                throw new IllegalArgumentException(
                        "You cannot reuse any of your last 5 passwords.");
            }
        }

        if (user.getPassword() != null) {
            passwordHistoryRepository.save(
                    PasswordHistory.builder()
                            .user(user)
                            .passwordHash(user.getPassword())
                            .changedAt(LocalDateTime.now())
                            .build()
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordExpiry(LocalDateTime.now().plusDays(90));

        userRepository.save(user);

        trimPasswordHistory(user);
    }

    private void trimPasswordHistory(User user) {

        List<PasswordHistory> history =
                passwordHistoryRepository.findByUserOrderByChangedAtDesc(user);

        if (history.size() <= 5) {
            return;
        }

        passwordHistoryRepository.deleteAll(
                history.subList(5, history.size())
        );
    }

    public String getLoggedInUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (principal instanceof UserDetails ud) ? ud.getUsername() : principal.toString();
    }

}
