package com.bloom.bloomschool.communication.repository;

import com.bloom.bloomschool.communication.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByOrderBySentAtDesc();
}
