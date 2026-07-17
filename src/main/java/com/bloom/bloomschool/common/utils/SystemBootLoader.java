package com.bloom.bloomschool.common.utils;

import com.bloom.bloomschool.auth.model.Permission;
import com.bloom.bloomschool.auth.model.Role;
import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.auth.model.UserPermission;
import com.bloom.bloomschool.auth.repo.RoleRepository;
import com.bloom.bloomschool.auth.repo.UserPermissionRepository;
import com.bloom.bloomschool.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
@Order(2)
public class SystemBootLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedUser("Admin001",   "School",  "Admin",   "admin@bloomschool.com",   "admin123",   "ADMIN");
        seedUser("Teacher001", "Demo",    "Teacher", "teacher@bloomschool.com", "teacher123", "TEACHER");
        seedUser("parent001",  "Demo",    "Parent",  "parent@bloomschool.com",  "parent123",  "PARENT");
    }

    protected void seedUser(String userName, String firstName, String otherNames,
                            String email, String rawPassword, String roleName) {
        if (userRepository.existsByUserName(userName)) {
            log.info("User '{}' already exists, skipping", userName);
            return;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException(roleName + " role not found — ensure DataSeeder ran first"));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = User.builder()
                .userName(userName)
                .firstName(firstName)
                .otherNames(otherNames)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .active(true)
                .firstLogin(false)
                .enable2FA(false)
                .roles(roles)
                .build();

        User saved = userRepository.save(user);

        for (Permission permission : role.getPermissions()) {
            userPermissionRepository.save(UserPermission.builder()
                    .user(saved)
                    .permission(permission)
                    .overrideType(null)
                    .build());
        }

        log.info("User '{}' with role '{}' created successfully", userName, roleName);
    }
}
