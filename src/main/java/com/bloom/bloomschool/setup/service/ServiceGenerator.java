package com.bloom.bloomschool.setup.service;

import com.bloom.bloomschool.setup.entity.SystemSequence;
import com.bloom.bloomschool.setup.repository.SetupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ServiceGenerator {

    private final SetupRepository repo;
    @Transactional
    public String generateReference(String sequenceType){
        LocalDate today = LocalDate.now();
        SystemSequence setup = repo.findBySequenceTypeAndSequenceDate(sequenceType, today)
                .orElseGet(() -> repo.save(SystemSequence.builder()
                        .sequenceType(sequenceType)
                        .sequenceDate(today)
                        .lastSequence(0)
                        .build()));
        setup.setLastSequence(setup.getLastSequence() + 1);
        repo.save(setup);
        return String.format("%s-%s-%03d", sequenceType.toUpperCase(),
                today.format(DateTimeFormatter.ofPattern("yyMMdd")), setup.getLastSequence());
    }

    @Transactional
    public String generateYearlyReference(String sequenceType) {
        LocalDate yearStart = LocalDate.now().withDayOfYear(1);

        SystemSequence seq = repo.findBySequenceTypeAndSequenceDate(sequenceType, yearStart)
                .orElseGet(() -> repo.save(SystemSequence.builder()
                        .sequenceType(sequenceType)
                        .sequenceDate(yearStart)
                        .lastSequence(0)
                        .build()));

        seq.setLastSequence(seq.getLastSequence() + 1);
        repo.save(seq);

        return String.format("%s-%d-%04d",
                sequenceType.toUpperCase(), yearStart.getYear(), seq.getLastSequence());
    }
}
