package com.bloom.bloomschool.student.repository;

import com.bloom.bloomschool.student.entity.Admissions;
import com.bloom.bloomschool.student.enums.ApplicationStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AdmissionsRepository extends JpaRepository<Admissions, UUID> {
    Optional<Admissions> findByUuid(UUID uuid);

    @Query("SELECT COUNT(app) FROM Admissions app WHERE app.gradeEntity.uuid = :gradeUuid AND app.stream = :stream AND app.applicationStage = :applicationStage")
    int getOccupiedStreamCapacity(@Param("gradeUuid") UUID gradeUuid,
                                   @Param("stream") String stream,
                                   @Param("applicationStage") ApplicationStage stage);
}
