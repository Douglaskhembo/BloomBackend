package com.bloom.bloomschool.paymentTest.repository;

import com.bloom.bloomschool.paymentTest.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
}
