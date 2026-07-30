package com.bloom.bloomschool.subjects.service;

import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.school.repository.GradeLevelRepository;
import com.bloom.bloomschool.subjects.dto.SubjectRequest;
import com.bloom.bloomschool.subjects.entity.Subject;
import com.bloom.bloomschool.subjects.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepo;
    private final GradeLevelRepository gradeLevelRepo;

    public List<Subject> getAll() {
        return subjectRepo.findAll();
    }

    @Transactional
    public Subject create(SubjectRequest req) {
        if (subjectRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Subject '" + req.getName() + "' already exists");
        return subjectRepo.save(Subject.builder()
                .name(req.getName())
                .code(req.getCode())
                .description(req.getDescription())
                .active(req.isActive())
                .gradeLevels(resolveGradeLevels(req.getGradeLevelUuids()))
                .build());
    }

    @Transactional
    public Subject update(Long id, SubjectRequest req) {
        Subject s = subjectRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        s.setName(req.getName());
        s.setCode(req.getCode());
        s.setDescription(req.getDescription());
        s.setActive(req.isActive());
        s.setGradeLevels(resolveGradeLevels(req.getGradeLevelUuids()));
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

    private Set<GradeLevel> resolveGradeLevels(Set<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) return new HashSet<>();
        return new HashSet<>(gradeLevelRepo.findAllByUuidIn(uuids));
    }
}
