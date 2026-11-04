package com.bloom.bloomschool.users.repository;

import com.bloom.bloomschool.users.entity.Permissions;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Long> {
    @Query(value = """
            select * from bloom_school_permissions p where p.uuid  = :uuid
            """, nativeQuery = true)
    Optional<Permissions> findByUuid(@Param("uuid") UUID uuid);

    @Query(value = """
            select * from bloom_school_permissions p where p.name  = :name
            """, nativeQuery = true)
    Optional<Permissions> findByName(@Param("name") String name);

    @Query(value = """
            select count(*) > 0 from bloom_school_permissions p where p.name  = :name
            """, nativeQuery = true)
    boolean existsByName(@Param("name") String name);

}
