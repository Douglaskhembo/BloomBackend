package com.bloom.bloomschool.school.repository;

import com.bloom.bloomschool.school.entity.SchoolInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolInformationRepository extends JpaRepository<SchoolInformation, Long> {

    Optional<SchoolInformation> findByUuid(UUID uuid);

    @Query("SELECT COUNT(s) > 0 FROM SchoolInformation s WHERE s.schoolAddress = ?1")
    boolean existsBySchoolAddress(String schoolAddress);

    @Query("SELECT COUNT(s) > 0 FROM SchoolInformation s WHERE s.schoolCode = ?1")
    boolean existsBySchoolCode(String schoolCode);
}