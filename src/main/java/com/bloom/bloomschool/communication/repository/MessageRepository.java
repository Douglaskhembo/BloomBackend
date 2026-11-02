package com.bloom.bloomschool.communication.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bloom.bloomschool.communication.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByUuid(UUID uuid);

    List<Message> findAll();
}
