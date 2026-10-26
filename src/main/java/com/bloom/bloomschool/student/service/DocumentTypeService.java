package com.bloom.bloomschool.student.service;


import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.student.dto.DocumentTypeDto;
import com.bloom.bloomschool.student.entity.DocumentType;
import com.bloom.bloomschool.student.enums.DocumentCategory;
import com.bloom.bloomschool.student.enums.OwnerType;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.student.repository.DocumentTypeRepository;
import com.bloom.bloomschool.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentTypeService {
    private final DocumentTypeRepository repo;
    private final StudentRepository studentRepo;
    private final StaffRepository staffRepo;
    @Transactional(readOnly = true)
    public List<DocumentTypeDto> getAllDocuments(){
        return repo.findAll().stream().map(this::convertToDto).toList();
    }

    @Transactional(readOnly = true)
    public DocumentTypeDto getDocument(UUID uuid){
        DocumentType documentType = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document type does not exist"));
        return convertToDto(documentType);
    }
    private DocumentTypeDto convertToDto(DocumentType body){
        return DocumentTypeDto.builder()
                .name(body.getName())
                .description(body.getDescription())
                .mandatory(body.isMandatory())
                .active(body.isActive())
                .documentCategory(body.getDocumentCategory())
                .ownerType(body.getOwnerType())
                .build();
    }

    public DocumentTypeDto createDocument(DocumentTypeDto req){
        if(repo.existsByName(req.getName())){
            throw new RuntimeException("Document "+req.getName()+" already exists");
        }
        DocumentType documentType=  repo.save(DocumentType.builder()
                .name(req.getName())
                .description(req.getDescription())
                .mandatory(req.isMandatory())
                .active(req.isActive())
                .documentCategory(req.getDocumentCategory())
                .ownerType(req.getOwnerType())
                .build());
        return convertToDto(documentType);
    }

    public DocumentTypeDto updateDocument(UUID uuid, DocumentTypeDto req){
        DocumentType existingDocument = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document type does not exist"));

        existingDocument.setDocumentCategory(req.getDocumentCategory());
        existingDocument.setName(req.getName());
        existingDocument.setDescription(req.getDescription());
        existingDocument.setMandatory(req.isMandatory());
        existingDocument.setOwnerType(req.getOwnerType());
        DocumentType documentType=  repo.save(existingDocument);
        return convertToDto(documentType);
    }

    public void toggleStatus(UUID uuid){
        DocumentType existingDocument = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document type does not exist"));

        existingDocument.setActive(!existingDocument.isActive());
        repo.save(existingDocument);
    }

    public void deleteDocumentType(UUID uuid){
        DocumentType existingDocument = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document type does not exist"));

        repo.delete(existingDocument);
    }

    public List<DocumentType> getMandatory(OwnerType ownerType, UUID ownerUuid){
        DocumentCategory category = ownerType == OwnerType.STAFF ? DocumentCategory.STAFF : DocumentCategory.STUDENT;
        List<DocumentType> mandatory = repo.getMandatory(category);

        Set<UUID> providedDocuments = ownerType == OwnerType.STAFF ?
                new HashSet<>(staffRepo.getDocumentUuids(ownerUuid))
                : new HashSet<>(studentRepo.getDocumentUuids(ownerUuid));

        return mandatory.stream()
                .filter(dt -> !providedDocuments.contains(dt.getUuid()))
                .collect(Collectors.toList());
    }
}
