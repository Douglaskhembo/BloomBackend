package com.bloom.bloomschool.document.service;


import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.document.dto.DocumentTypeDto;
import com.bloom.bloomschool.document.entity.DocumentType;
import com.bloom.bloomschool.document.enums.DocumentCategory;
import com.bloom.bloomschool.document.enums.OwnerType;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.document.repository.DocumentTypeRepository;
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
                .uuid(body.getUuid())
                .name(body.getName())
                .description(body.getDescription())
                .mandatory(body.isMandatory())
                .active(body.isActive())
                .documentCategory(body.getDocumentCategory())
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

    public List<DocumentType> getMandatory(DocumentCategory category){
        return repo.getMandatory(category);
    }
}
