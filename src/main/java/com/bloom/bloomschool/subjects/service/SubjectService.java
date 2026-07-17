package com.bloom.bloomschool.subjects.service;

import com.bloom.bloomschool.subjects.dto.SubjectRequest;
import com.bloom.bloomschool.subjects.entity.Subject;
import com.bloom.bloomschool.subjects.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepo;

    public List<Subject> getAll(String grade) {
        if (grade != null) return subjectRepo.findByGrade(grade);
        return subjectRepo.findAll();
    }

    @Transactional
    public Subject create(SubjectRequest req) {
        if (subjectRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Subject '" + req.getName() + "' already exists");
        return subjectRepo.save(Subject.builder()
                .name(req.getName())
                .code(req.getCode())
                .grade(req.getGrade())
                .description(req.getDescription())
                .build());
    }

    @Transactional
    public Subject update(Long id, SubjectRequest req) {
        Subject s = subjectRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        s.setName(req.getName());
        s.setCode(req.getCode());
        s.setGrade(req.getGrade());
        s.setDescription(req.getDescription());
        return subjectRepo.save(s);
    }

    @Transactional
    public Subject toggle(Long id) {
        Subject s = subjectRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        s.setActive(!s.isActive());
        return subjectRepo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        subjectRepo.deleteById(id);
    }
}
