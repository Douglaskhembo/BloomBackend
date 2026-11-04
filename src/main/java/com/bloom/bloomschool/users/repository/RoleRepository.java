package com.bloom.bloomschool.users.repository;

import com.bloom.bloomschool.users.entity.Roles;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
    @Query(value = """
            SELECT * FROM bloom_school_roles r WHERE r.uuid  = :uuid
            """, nativeQuery = true)
    Optional<Roles> findByUuid(@Param("uuid") UUID uuid);

    @Query(value = """
            SELECT * FROM bloom_school_roles r WHERE r.name  = :name
            """, nativeQuery = true)
    Optional<Roles> findByName(@Param("name") String name);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM bloom_school_roles r WHERE r.name  = :name
            """, nativeQuery = true)
    boolean existsByName(@Param("name") String name);
}
