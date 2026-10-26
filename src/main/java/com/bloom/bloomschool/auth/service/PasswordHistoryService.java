package com.bloom.bloomschool.auth.service;

import com.bloom.bloomschool.auth.dto.PasswordHistoryRequestDTO;
import com.bloom.bloomschool.auth.dto.PasswordHistoryResponseDTO;
import com.bloom.bloomschool.auth.entity.PasswordHistory;
import com.bloom.bloomschool.auth.repository.PasswordHistoryRepository;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PasswordHistoryService {
    private final PasswordHistoryRepository passHistoryRepo;

    private PasswordHistory findEntity(UUID uuid){
        return passHistoryRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("The Password History is unavailable"));
    }
    public List<PasswordHistoryResponseDTO>getAllPasswordHistory(){
        return passHistoryRepo.findAll().stream()
                .map(h -> PasswordHistoryResponseDTO.builder()
                        .uuid(h.getUuid())
                        .passwordHash(h.getPasswordHash())
                        .createdAt(h.getCreatedAt())
                        .build())
                .toList();
    }

    public PasswordHistoryResponseDTO getPasswordHistoryByUuid(UUID uuid){
        PasswordHistory h = findEntity(uuid);
        return PasswordHistoryResponseDTO.builder()
                .uuid(h.getUuid())
                .passwordHash(h.getPasswordHash())
                .createdAt(h.getCreatedAt())
                .build();
    }
    @Transactional
    public void createPasswordHistory(PasswordHistoryRequestDTO req){
        passHistoryRepo.save(PasswordHistory.builder()
                .passwordHash(req.getPasswordHash())
                .createdAt(req.getCreatedAt())
                .build());

    }
    @Transactional
    public void updatePasswordHistory(UUID uuid, PasswordHistoryRequestDTO req){
        PasswordHistory h = findEntity(uuid);
        h.setPasswordHash(req.getPasswordHash());
        h.setCreatedAt(req.getCreatedAt());
        passHistoryRepo.save(h);
    }
    @Transactional
    public void deletePasswordHistory(UUID uuid){
        PasswordHistory h = findEntity(uuid);
        passHistoryRepo.delete(h);
    }
}
