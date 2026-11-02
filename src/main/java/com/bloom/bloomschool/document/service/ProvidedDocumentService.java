package com.bloom.bloomschool.document.service;

import com.bloom.bloomschool.document.dto.DocumentTypeDto;
import com.bloom.bloomschool.document.dto.ProvidedDocumentDto;
import com.bloom.bloomschool.document.entity.DocumentType;
import com.bloom.bloomschool.document.entity.ProvidedDocument;
import com.bloom.bloomschool.document.enums.DocumentCategory;
import com.bloom.bloomschool.document.enums.OwnerType;
import com.bloom.bloomschool.document.repository.DocumentTypeRepository;
import com.bloom.bloomschool.document.repository.ProvidedDocumentRepository;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.student.repository.AdmissionsRepository;
import com.bloom.bloomschool.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvidedDocumentService {
    private final ProvidedDocumentRepository repo;
    private final DocumentTypeService documentTypeService;
    private final DocumentTypeRepository documentTypeRepository;
    private final AdmissionsRepository admissionsRepo;
    private final StudentRepository studentRepo;
    private final StaffRepository staffRepo;
    @Value("${app.uploads.document-folder:uploads/documents}")
    private String uploadFolder;
    @Transactional(readOnly = true)
    public List<ProvidedDocumentDto> getOwner(UUID ownerUuid){
        List<ProvidedDocument> providedDocument = repo.findAllByOwnerUuid(ownerUuid);

        return providedDocument.stream().map(this::convertToDto).toList();
    }

    private ProvidedDocumentDto convertToDto(ProvidedDocument body){
        DocumentType documentType = documentTypeRepository.findByUuid(body.getDocumentType().getUuid())
                .orElseThrow(() -> new RuntimeException("Document type not found"));
        DocumentTypeDto documentTypeDto = DocumentTypeDto.builder()
                .name(documentType.getName())
                .description(documentType.getDescription())
                .mandatory(documentType.isMandatory())
                .active(documentType.isActive())
                .documentCategory(documentType.getDocumentCategory())
                .build();

        return ProvidedDocumentDto.builder()
                .uuid(body.getUuid())
                .ownerType(body.getOwnerType())
                .ownerUuid(body.getOwnerUuid())
                .documentTypeDto(documentTypeDto)
                .fileName(body.getFileName())
                .filePath(body.getFilePath())
                .fileSize(body.getFileSize())
                .uploadDate(body.getUploadDate())
                .contentType(body.getContentType())
                .build();
    }
    @Transactional(readOnly = true)
    public List<DocumentType> getMissingMandatory(OwnerType ownerType, UUID ownerUuid){
        DocumentCategory category = ownerType == OwnerType.STAFF ? DocumentCategory.STAFF : DocumentCategory.STUDENT;
        List<DocumentType> mandatory = documentTypeService.getMandatory(category);

        List<UUID> providedDocumentsUuids = getByOwner(ownerType, ownerUuid).stream()
                .map(p -> p.getDocumentType().getUuid())
                .toList();

        return mandatory.stream()
                .filter(dt -> !providedDocumentsUuids.contains(dt.getUuid()))
                .collect(Collectors.toList());
    }

    private List<ProvidedDocument> getByOwner(OwnerType ownerType, UUID ownerUuid) {
        return repo.getByOwner(ownerType, ownerUuid);
    }

    @Transactional
    public void uploadDocument(OwnerType ownerType, UUID ownerUuid, UUID documentTypeUuid, MultipartFile file){
        if (file == null || file.isEmpty()){
            throw new RuntimeException("File cannot be empty");
        }

        DocumentType documentType = documentTypeRepository.findByUuid(documentTypeUuid)
                        .orElseThrow(() -> new RuntimeException("Document type not found"));

        boolean ownerExists = ownerType == OwnerType.STAFF
                ? staffRepo.findByUuid(ownerUuid).isPresent()
                : admissionsRepo.findByUuid(ownerUuid).isPresent() || studentRepo.findByUuid(ownerUuid).isPresent();
        if (!ownerExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ownerType + " not found");
        }

        if (repo.existsByOwnerTypeAndOwnerUuidAndDocumentType_Uuid(ownerType, ownerUuid, documentTypeUuid)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This owner already has a " + documentType.getName() + " uploaded");
        }

        try {
            String folder = uploadFolder + "/"+ ownerType.name().toLowerCase()+"_"+ownerUuid;
            Files.createDirectories(Paths.get(folder));

            String safeFileName = file.getOriginalFilename() != null
                    ? file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.\\-]", "_")
                    : "file_" + System.currentTimeMillis();
            String storedName = System.currentTimeMillis() + "_" + safeFileName;
            Path target = Paths.get(folder, storedName);
            Files.write(target, file.getBytes());

        repo.save(ProvidedDocument.builder()
                .ownerType(ownerType)
                .ownerUuid(ownerUuid)
                .documentType(documentType)
                .fileName(safeFileName)
                .filePath(target.toString())
                .fileSize(file.getSize())
                .uploadDate(LocalDateTime.now())
                .contentType(file.getContentType())
                .build());

        }catch(IOException e){
            throw new UncheckedIOException("Failed to store uploaded document: " + e.getMessage(), e);
        }
    }


    @Transactional(readOnly = true)
    public byte[] downloadDocument(UUID uuid) {
        ProvidedDocument providedDocument = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        try {
            return Files.readAllBytes(Paths.get(providedDocument.getFilePath()));
        }catch (IOException e){ throw new UncheckedIOException("Failed to retrieve document: "+e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteDocument(UUID uuid){
        ProvidedDocument providedDocument = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        repo.delete(providedDocument);
        try{
            Files.deleteIfExists(Paths.get(providedDocument.getFilePath()));
        }catch (IOException ignored){}
    }

    public ProvidedDocument getByDocumentUuid(UUID uuid){
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
    }
}
