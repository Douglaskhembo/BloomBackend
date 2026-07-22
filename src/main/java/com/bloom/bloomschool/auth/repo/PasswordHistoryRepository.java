package com.bloom.bloomschool.auth.repo;

import com.bloom.bloomschool.auth.model.PasswordHistory;
import com.bloom.bloomschool.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, UUID> {

    List<PasswordHistory> findTop5ByUserOrderByChangedAtDesc(User user);

    List<PasswordHistory> findByUserOrderByChangedAtDesc(User user);
}
