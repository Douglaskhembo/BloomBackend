package com.bloom.bloomschool.document.service;


import com.bloom.bloomschool.document.dto.DocumentTypeRequestDto;
import com.bloom.bloomschool.document.dto.DocumentTypeResponseDto;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.document.entity.DocumentType;
import com.bloom.bloomschool.document.enums.DocumentCategory;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.document.repository.DocumentTypeRepository;
import com.bloom.bloomschool.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentTypeService {
    private final DocumentTypeRepository repo;
    private final StudentRepository studentRepo;
    private final StaffRepository staffRepo;
    @Transactional(readOnly = true)
    public List<DocumentTypeResponseDto> getAllDocuments(){
        return repo.findAll().stream().map(this::convertToDto).toList();
    }

    @Transactional(readOnly = true)
    public DocumentTypeResponseDto getDocument(UUID uuid){
        DocumentType documentType = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document type does not exist"));
        return convertToDto(documentType);
    }
    private DocumentTypeResponseDto convertToDto(DocumentType body){
        return DocumentTypeResponseDto.builder()
                .uuid(body.getUuid())
                .name(body.getName())
                .description(body.getDescription())
                .mandatory(body.isMandatory())
                .active(body.isActive())
                .documentCategory(body.getDocumentCategory())
                .build();
    }

    public DocumentTypeResponseDto createDocument(DocumentTypeRequestDto req){
        if(repo.existsByName(req.getName())){
            throw new RuntimeException("Document "+req.getName()+" already exists");
        }
        DocumentType documentType=  repo.save(DocumentType.builder()
                .name(req.getName())
                .description(req.getDescription())
                .mandatory(req.getMandatory())
                .active(req.getActive())
                .documentCategory(req.getDocumentCategory())
                .build());
        return convertToDto(documentType);
    }

    public DocumentTypeResponseDto updateDocument(UUID uuid, DocumentTypeRequestDto req){
        DocumentType existingDocument = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document type does not exist"));

        existingDocument.setDocumentCategory(req.getDocumentCategory());
        existingDocument.setName(req.getName());
        existingDocument.setDescription(req.getDescription());
        existingDocument.setMandatory(req.getMandatory());
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
