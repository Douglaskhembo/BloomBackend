package com.bloom.bloomschool.communication.repository;

import com.bloom.bloomschool.communication.entity.MessageRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRecipientRepository extends JpaRepository<MessageRecipient, Long> {
    Optional<MessageRecipient> findByUuid(UUID uuid);
    List<MessageRecipient> findByRecipientEmailOrderByReceivedAtDesc(String recipientEmail);
    long countByRecipientEmailAndReadFalse(String recipientEmail);
}
