package com.bloom.bloomschool.setups.repository;

import com.bloom.bloomschool.setups.entity.SystemSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SystemSequenceRepository extends JpaRepository<SystemSequence, Long> {

    Optional<SystemSequence> findBySequenceTypeAndSequenceDate(String sequenceType, LocalDate sequenceDate);
    Optional<SystemSequence> findBySequenceType(String sequenceType);
}
