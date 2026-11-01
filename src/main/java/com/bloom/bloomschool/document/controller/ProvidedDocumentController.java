package com.bloom.bloomschool.document.controller;

import com.bloom.bloomschool.document.dto.ProvidedDocumentDto;
import com.bloom.bloomschool.document.entity.DocumentType;
import com.bloom.bloomschool.document.entity.ProvidedDocument;
import com.bloom.bloomschool.document.enums.OwnerType;
import com.bloom.bloomschool.document.service.ProvidedDocumentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.ServerRequest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providedDocuments")
@RequiredArgsConstructor
public class ProvidedDocumentController {
    private final ProvidedDocumentService service;

    @GetMapping("/owner/{uuid}")
    public ResponseEntity<List<ProvidedDocumentDto>> getOwner(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getOwner(uuid));
    }

    @GetMapping("/missingMandatory")
    public ResponseEntity<List<DocumentType>> getMissingMandatory(
            @RequestParam OwnerType ownerType,
            @RequestParam UUID ownerUuid) {
        return ResponseEntity.ok(service.getMissingMandatory(ownerType, ownerUuid));
    }

    @PostMapping(value = "/uploadDocument", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(@RequestParam OwnerType ownerType,
                                            @RequestParam UUID ownerUuid,
                                            @RequestParam UUID documentTypeUuid,
                                            @RequestPart("file") MultipartFile file) {
        service.uploadDocument(ownerType,  ownerUuid, documentTypeUuid, file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/downloadDocument/{uuid}")
    public void downloadDocument(@PathVariable UUID uuid, HttpServletResponse response) {
        ProvidedDocument providedDocument = service.getByDocumentUuid(uuid);
        byte[] bytes = service.downloadDocument(uuid);
        response.setContentType(providedDocument.getContentType() != null
                ? providedDocument.getContentType() :MediaType.APPLICATION_OCTET_STREAM_VALUE);

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""+providedDocument.getFileName()+"\"" );
        response.setContentLength(bytes.length);
        try{
            response.getOutputStream().write(bytes);
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    @DeleteMapping("/deleteDocument/{uuid}")
    public ResponseEntity<?> deleteDocument(@PathVariable UUID uuid) {
        service.deleteDocument(uuid);
        return ResponseEntity.noContent().build();
    }
}