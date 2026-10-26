package com.bloom.bloomschool.staff.repository;

import com.bloom.bloomschool.staff.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, UUID> {

    Optional<StaffEntity> findByUuid(UUID Uuid);

    boolean existsByEmailAndUuidNot(String email, UUID uuid);
    boolean existsByPhoneNumberAndUuidNot(String phoneNumber, UUID uuid);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT s.document_type_uuid FROM StaffEntity s WHERE s.uuid =:StaffUuid")
    List<UUID> getDocumentUuids(@Param("StaffUuid") UUID staffUuid);

}
