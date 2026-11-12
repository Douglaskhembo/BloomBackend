package com.bloom.bloomschool.document.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.document.dto.DocumentTypeRequestDto;
import com.bloom.bloomschool.document.dto.DocumentTypeResponseDto;
import com.bloom.bloomschool.document.service.DocumentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documentType")
@RequiredArgsConstructor
public class DocumentTypeController {
    private final DocumentTypeService service;

    @GetMapping("/get-all-document-types")
    public ResponseEntity<ApiResponse<List<DocumentTypeResponseDto>>> getAllDocumentTypes() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAllDocuments()));
    }

    @GetMapping("/get-document-type/{uuid}")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDto>> getDocumentType(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.getDocument(uuid)));
    }

    @PostMapping("/create-document-type")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDto>> createDocumentType(@RequestBody DocumentTypeRequestDto req) {
        return ResponseEntity.ok(ApiResponse.ok("Document type created", service.createDocument(req)));
    }

    @PutMapping("/update-document-type/{uuid}")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDto>> updateDocumentType(@PathVariable UUID uuid, @RequestBody DocumentTypeRequestDto req) {
        return ResponseEntity.ok(ApiResponse.ok("Document type updated", service.updateDocument(uuid, req)));
    }

    @PatchMapping("/toggle-document-type/{uuid}")
    public ResponseEntity<ApiResponse<?>> toggleDocumentType(@PathVariable UUID uuid) {
        service.toggleStatus(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Document type status updated"));
    }

    @DeleteMapping("/delete-document-type/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteDocumentType(@PathVariable UUID uuid) {
        service.deleteDocumentType(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Document type deleted"));
    }
}