package com.bloom.bloomschool.communication.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.communication.dto.SendMessageRequest;
import com.bloom.bloomschool.communication.service.CommunicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/communication")
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationService communicationService;

    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<?>> getAllMessages() {
        return ResponseEntity.ok(ApiResponse.ok(communicationService.getAllMessages()));
    }

    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<?>> sendMessage(@Valid @RequestBody SendMessageRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Message sent", communicationService.sendMessage(req)));
    }

    @GetMapping("/inbox")
    public ResponseEntity<ApiResponse<?>> getInbox() {
        return ResponseEntity.ok(ApiResponse.ok(communicationService.getInbox()));
    }

    @GetMapping("/inbox/unread-count")
    public ResponseEntity<ApiResponse<?>> getUnreadCount() {
        return ResponseEntity.ok(ApiResponse.ok(communicationService.getUnreadCount()));
    }

    @PatchMapping("/inbox/{uuid}/read")
    public ResponseEntity<ApiResponse<?>> markRead(@PathVariable UUID uuid) {
        communicationService.markRead(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Marked as read"));
    }
}
