package com.bloom.bloomschool.auth.controller;

import com.bloom.bloomschool.auth.dto.PasswordHistoryRequestDTO;
import com.bloom.bloomschool.auth.dto.PasswordHistoryResponseDTO;
import com.bloom.bloomschool.auth.service.PasswordHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/v1/passwordHistory")
@RestController
public class PasswordHistoryController {
    private final PasswordHistoryService passHistoryService;

    @GetMapping
    public ResponseEntity<List<PasswordHistoryResponseDTO>>getAllPasswordHistory(){
        return ResponseEntity.ok(passHistoryService.getAllPasswordHistory());
    }
    @GetMapping("/{uuid}")
    public ResponseEntity<PasswordHistoryResponseDTO> getPasswordHistoryByUuid(@PathVariable UUID uuid){
        return ResponseEntity.ok(passHistoryService.getPasswordHistoryByUuid(uuid));
    }

    @PostMapping
    public ResponseEntity<?> createPasswordHistory(@RequestBody PasswordHistoryRequestDTO req){
        passHistoryService.createPasswordHistory(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> updatePasswordHistory(@PathVariable UUID uuid, @RequestBody PasswordHistoryRequestDTO req){
        passHistoryService.updatePasswordHistory(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deletePasswordHistory(@PathVariable UUID uuid){
        passHistoryService.deletePasswordHistory(uuid);
        return ResponseEntity.noContent().build();
    }
}
