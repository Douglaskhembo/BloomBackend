package com.bloom.bloomschool.document.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.document.dto.DocumentTypeResponseDto;
import com.bloom.bloomschool.document.dto.ProvidedDocumentResponseDto;
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providedDocument")
@RequiredArgsConstructor
public class ProvidedDocumentController {
    private final ProvidedDocumentService service;

    @GetMapping("/get-provided-documents-by-owner/{uuid}")
    public ResponseEntity<ApiResponse<List<ProvidedDocumentResponseDto>>> getProvidedDocumentsByOwner(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.getOwner(uuid)));
    }

    @GetMapping("/get-missing-mandatory-document-types")
    public ResponseEntity<ApiResponse<List<DocumentTypeResponseDto>>> getMissingMandatoryDocumentTypes(
            @RequestParam OwnerType ownerType,
            @RequestParam UUID ownerUuid) {
        return ResponseEntity.ok(ApiResponse.ok(service.getMissingMandatoryDtos(ownerType, ownerUuid)));
    }

    @PostMapping(value = "/upload-provided-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProvidedDocumentResponseDto>> uploadProvidedDocument(
            @RequestParam OwnerType ownerType,
            @RequestParam UUID ownerUuid,
            @RequestParam UUID documentTypeUuid,
            @RequestPart("file") MultipartFile file) {
        ProvidedDocumentResponseDto created = service.uploadDocument(ownerType, ownerUuid, documentTypeUuid, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Document uploaded", created));
    }

    @GetMapping("/download-provided-document/{uuid}")
    public void downloadProvidedDocument(@PathVariable UUID uuid, HttpServletResponse response) {
        ProvidedDocument providedDocument = service.getByDocumentUuid(uuid);
        byte[] bytes = service.downloadDocument(uuid);
        response.setContentType(providedDocument.getContentType() != null
                ? providedDocument.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE);

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + providedDocument.getFileName() + "\"");
        response.setContentLength(bytes.length);
        try {
            response.getOutputStream().write(bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @DeleteMapping("/delete-provided-document/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteProvidedDocument(@PathVariable UUID uuid) {
        service.deleteDocument(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Document deleted"));
    }
}