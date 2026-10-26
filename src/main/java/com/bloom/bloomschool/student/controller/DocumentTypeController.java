package com.bloom.bloomschool.student.controller;

import com.bloom.bloomschool.student.dto.DocumentTypeDto;
import com.bloom.bloomschool.student.service.DocumentTypeService;
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
    public ResponseEntity<List<DocumentTypeDto>> getAll() {
        return ResponseEntity.ok(service.getAllDocuments());
    }

    @GetMapping("/documentType/{uuid}")
    public ResponseEntity<DocumentTypeDto> getDocumentType(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getDocument(uuid));
    }

    @PostMapping("/createDocumentType")
    public ResponseEntity<DocumentTypeDto> create(@RequestBody DocumentTypeDto req) {
        return ResponseEntity.ok(service.createDocument( req));
    }

    @PutMapping("/updateDocumentType/{uuid}")
    public ResponseEntity<DocumentTypeDto> update(@PathVariable UUID uuid, @RequestBody DocumentTypeDto req) {
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
