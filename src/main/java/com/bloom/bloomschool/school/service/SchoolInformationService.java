package com.bloom.bloomschool.school.service;

import com.bloom.bloomschool.school.dto.*;
import com.bloom.bloomschool.school.entity.SchoolInformation;
import com.bloom.bloomschool.school.repository.SchoolInformationRepository;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolInformationService {

    private final SchoolInformationRepository schoolRepo;

    private SchoolInformation findEntity(UUID uuid) {
        return schoolRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("School information not found"));
    }

    public List<SchoolInformationResponseDTO> getAllSchools() {
        return schoolRepo.findAll().stream()
                .map(s -> SchoolInformationResponseDTO.builder()
                        .uuid(s.getUuid())
                        .schoolName(s.getSchoolName())
                        .registrationNumber(s.getRegistrationNumber())
                        .schoolCode(s.getSchoolCode())
                        .schoolEmail(s.getSchoolEmail())
                        .schoolPhone(s.getSchoolPhone())
                        .schoolAddress(s.getSchoolAddress())
                        .website(s.getWebsite())
                        .establishedYear(s.getEstablishedYear())
                        .logoUrl(s.getLogoUrl())
                        .hasBranch(s.isHasBranch())
                        .hasDepartment(s.isHasDepartment())
                        .build())
                .toList();
    }

    public SchoolInformationResponseDTO getSchoolByUuid(UUID uuid) {
        SchoolInformation s = findEntity(uuid);
        return SchoolInformationResponseDTO.builder()
                .uuid(s.getUuid())
                .schoolName(s.getSchoolName())
                .registrationNumber(s.getRegistrationNumber())
                .schoolCode(s.getSchoolCode())
                .schoolEmail(s.getSchoolEmail())
                .schoolPhone(s.getSchoolPhone())
                .schoolAddress(s.getSchoolAddress())
                .website(s.getWebsite())
                .establishedYear(s.getEstablishedYear())
                .logoUrl(s.getLogoUrl())
                .hasBranch(s.isHasBranch())
                .hasDepartment(s.isHasDepartment())
                .build();
    }

    @Transactional
    public void createSchool(SchoolInformationRequestDTO req) {
        if (schoolRepo.existsBySchoolCode(req.getSchoolCode())) {
            throw new RuntimeException("School with this code already exists!!");
        }
        if (schoolRepo.existsBySchoolAddress(req.getSchoolAddress())) {
            throw new RuntimeException("School with this address already exists!!");
        }

        schoolRepo.save(SchoolInformation.builder()
                .schoolName(req.getSchoolName())
                .registrationNumber(req.getRegistrationNumber())
                .schoolCode(req.getSchoolCode())
                .schoolEmail(req.getSchoolEmail())
                .schoolPhone(req.getSchoolPhone())
                .schoolAddress(req.getSchoolAddress())
                .website(req.getWebsite())
                .establishedYear(req.getEstablishedYear())
                .logoUrl(req.getLogoUrl())
                .hasBranch(req.isHasBranch())
                .hasDepartment(req.isHasDepartment())
                .build());
    }

    @Transactional
    public void updateSchool(UUID uuid, SchoolInformationRequestDTO req) {
        SchoolInformation s = findEntity(uuid);
        s.setSchoolName(req.getSchoolName());
        s.setRegistrationNumber(req.getRegistrationNumber());
        s.setSchoolCode(req.getSchoolCode());
        s.setSchoolEmail(req.getSchoolEmail());
        s.setSchoolPhone(req.getSchoolPhone());
        s.setSchoolAddress(req.getSchoolAddress());
        s.setWebsite(req.getWebsite());
        s.setEstablishedYear(req.getEstablishedYear());
        s.setLogoUrl(req.getLogoUrl());
        s.setHasBranch(req.isHasBranch());
        s.setHasDepartment(req.isHasDepartment());

        schoolRepo.save(s);
    }

    @Transactional
    public void toggleBranchStatus(UUID uuid) {
        SchoolInformation school = schoolRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("School information not found"));

        school.setHasBranch(!school.isHasBranch());
        schoolRepo.save(school);
    }

    @Transactional
    public void toggleDepartmentStatus(UUID uuid) {
        SchoolInformation school = schoolRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("School information not found"));

        school.setHasDepartment(!school.isHasDepartment());
        schoolRepo.save(school);
    }

    @Transactional
    public void deleteSchool(UUID uuid) {
        SchoolInformation s = findEntity(uuid);
        schoolRepo.delete(s);
    }
}