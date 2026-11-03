package com.bloom.bloomschool.document.controller;

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
@RequestMapping("/api/v1/documentTypes")
@RequiredArgsConstructor
public class DocumentTypeController {
    private final DocumentTypeService service;

    @GetMapping("/allDocumentTypes")
    public ResponseEntity<List<DocumentTypeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAllDocuments());
    }

    @GetMapping("/documentType/{uuid}")
    public ResponseEntity<DocumentTypeResponseDto> getDocumentType(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getDocument(uuid));
    }

    @PostMapping("/createDocumentType")
    public ResponseEntity<DocumentTypeResponseDto> create(@RequestBody DocumentTypeRequestDto req) {
        return ResponseEntity.ok(service.createDocument( req));
    }

    @PutMapping("/updateDocumentType/{uuid}")
    public ResponseEntity<DocumentTypeResponseDto> update(@PathVariable UUID uuid, @RequestBody DocumentTypeRequestDto req) {
        return ResponseEntity.ok(service.updateDocument(uuid, req));
    }

    @PatchMapping("/toggleStatus/{uuid}")
    public ResponseEntity<?> toggleStatus(@PathVariable UUID uuid) {
        service.toggleStatus(uuid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteDocumentType/{uuid}")
    public ResponseEntity<?> deleteDocumentType(@PathVariable UUID uuid) {
        service.deleteDocumentType(uuid);
        return ResponseEntity.noContent().build();
    }
}
