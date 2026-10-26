package com.bloom.bloomschool.feestructure.repository;

import com.bloom.bloomschool.feestructure.entity.FeeItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeeItemRepository extends JpaRepository<FeeItemEntity, UUID> {
    Optional<FeeItemEntity> findByUuid(UUID uuid);
    boolean existsByName(String name);
    boolean existsByNameAndUuidNot(String name, UUID uuid);
}
