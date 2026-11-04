package com.bloom.bloomschool.users.repository;

import com.bloom.bloomschool.users.entity.Modules;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModulesRepository extends JpaRepository<Modules, Long> {
    @Query(value = """
            SELECT * FROM bloom_school_modules m WHERE m.uuid = :uuid
            """, nativeQuery = true)
    Optional<Modules>findByUuid(@Param("uuid") UUID uuid);

    @Query(value = """
           SELECT * FROM bloom_school_modules m WHERE m.name = :name
           """, nativeQuery = true)
    Optional<Modules> findByName(@Param("name") String name);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM bloom_school_modules m WHERE m.name = :name
            """, nativeQuery = true)
    boolean existsByName(@Param("name") String name);
}
