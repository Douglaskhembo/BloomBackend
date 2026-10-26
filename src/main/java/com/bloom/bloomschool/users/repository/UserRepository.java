package com.bloom.bloomschool.users.repository;

import com.bloom.bloomschool.users.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUuid(UUID uuid);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndEmail(String username, String email);

    boolean existsByEmail(String email);
}