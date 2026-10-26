package com.bloom.bloomschool.setup.repository;

import com.bloom.bloomschool.setup.entity.SystemSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SetupRepository extends JpaRepository<SystemSequence, Long> {
    Optional<SystemSequence> findBySequenceTypeAndSequenceDate(String sequenceType, LocalDate today);
}
