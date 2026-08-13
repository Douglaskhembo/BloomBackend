package com.bloom.bloomschool.setups.service;

import com.bloom.bloomschool.setups.entity.SystemSequence;
import com.bloom.bloomschool.setups.repository.SystemSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class RefGeneratorService {

    private final SystemSequenceRepository sequenceRepo;

    @Transactional
    public String generateReference(String sequenceType) {
        LocalDate today = LocalDate.now();
        SystemSequence sequence = sequenceRepo.findBySequenceTypeAndSequenceDate(sequenceType, today)
                .orElseGet(() -> sequenceRepo.save(SystemSequence.builder()
                        .sequenceType(sequenceType).sequenceDate(today).lastSequence(0).build()));
        sequence.setLastSequence(sequence.getLastSequence() + 1);
        sequenceRepo.save(sequence);
        return String.format("%s-%s-%03d", sequenceType.toUpperCase(),
                today.format(DateTimeFormatter.ofPattern("yyMMdd")), sequence.getLastSequence());
    }

    @Transactional
    public String generateCounter(String sequenceType) {
        SystemSequence sequence = sequenceRepo.findBySequenceType(sequenceType)
                .orElseGet(() -> sequenceRepo.save(SystemSequence.builder()
                        .sequenceType(sequenceType).lastSequence(0).build()));
        sequence.setLastSequence(sequence.getLastSequence() + 1);
        sequenceRepo.save(sequence);
        return String.format("%02d", sequence.getLastSequence());
    }
}
