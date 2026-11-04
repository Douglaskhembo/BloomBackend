package com.bloom.bloomschool.users.repository;

import com.bloom.bloomschool.users.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = """
            SELECT * FROM bloom_school_user u WHERE u.uuid = :uuid
            """, nativeQuery = true)
    Optional<User> findByUuid(@Param("uuid") UUID uuid);

    @Query(value = """
            SELECT * FROM bloom_school_user u WHERE u.email = :email
            """, nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = "SELECT COUNT(*) > 0 FROM bloom_school_user u WHERE u.username = :username", nativeQuery = true)
    boolean existsByUsername(@Param("username") String username);

    @Query(value = "SELECT COUNT(*) > 0 FROM bloom_school_user u WHERE u.email = :?1", nativeQuery = true)
    boolean existsByEmail(String email);

    @Query(value = """
            SELECT * FROM bloom_school_user u WHERE u.profile_ref = :profileRef
            """, nativeQuery = true)
    Optional<User> findByProfileRef(@Param("profileRef") String profileRef);

    @Query(value = "SELECT COUNT(*) > 0 FROM bloom_school_user u WHERE u.profile_ref = :?1", nativeQuery = true)
    boolean existsByProfileRef(String profileRef);
}