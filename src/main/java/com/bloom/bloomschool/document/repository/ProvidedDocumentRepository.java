package com.bloom.bloomschool.document.repository;

import com.bloom.bloomschool.document.entity.ProvidedDocument;
import com.bloom.bloomschool.document.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProvidedDocumentRepository extends JpaRepository<ProvidedDocument, UUID> {
    List<ProvidedDocument> findAllByOwnerUuid(UUID ownerUuid);

    @Query("SELECT pd FROM ProvidedDocument pd WHERE pd.ownerType = :ownerType AND pd.ownerUuid = :ownerUuid")
    List<ProvidedDocument> getByOwner(@Param("ownerType") OwnerType ownerType,
            @Param("ownerUuid") UUID ownerUuid);

    boolean existsByOwnerTypeAndOwnerUuidAndDocumentType_Uuid(
            OwnerType ownerType, UUID ownerUuid, UUID documentTypeUuid);

    Optional<ProvidedDocument> findByUuid(UUID uuid);
}
