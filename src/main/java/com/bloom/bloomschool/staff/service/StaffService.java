package com.bloom.bloomschool.staff.service;

import com.bloom.bloomschool.gradeLevel.dto.GradeSummaryDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.gradeLevel.repository.GradeRepository;
import com.bloom.bloomschool.staff.dto.StaffRequestDto;
import com.bloom.bloomschool.staff.dto.StaffResponseDto;
import com.bloom.bloomschool.staff.dto.StaffRoleResponseDto;
import com.bloom.bloomschool.staff.entity.StaffEntity;
import com.bloom.bloomschool.staff.entity.StaffRole;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.staff.repository.StaffRoleRepository;
import com.bloom.bloomschool.student.dto.SubjectSummaryDto;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.subject.entity.SubjectEntity;
import com.bloom.bloomschool.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService{

    private final StaffRepository repo;
    private final SubjectRepository subjectRepo;
    private final StaffRoleRepository staffRoleRepository;
    private final GradeRepository gradeRepo;
    @Transactional
    public List<StaffResponseDto> getAll(){
        List<StaffEntity> staffEntity = repo.findAll();
        if (CollectionUtils.isEmpty(staffEntity)){
            return List.of();
        }
        return staffEntity.stream()
                .map(this::convertToDto).toList();
    }

    @Transactional
    public StaffResponseDto getByUuid(UUID uuid) {
        return convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No staff found")));
    }

    private StaffResponseDto convertToDto(StaffEntity body){
        List<SubjectSummaryDto> subjectDto = body.getSubjects().stream()
                .map(dto -> SubjectSummaryDto.builder()
                        .uuid(dto.getUuid())
                        .name(dto.getName())
                        .subjectCode(dto.getSubjectCode())
                        .isActive(dto.getIsActive())
                                .build())
                .toList();
        StaffRoleResponseDto staffRoleResponseDto = new StaffRoleResponseDto();
        if(body.getRole() != null){
            StaffRole staffRole = body.getRole();
            staffRoleResponseDto = StaffRoleResponseDto.builder()
                    .uuid(staffRole.getUuid())
                    .name(staffRole.getName())
                    .description(staffRole.getDescription())
                    .status(staffRole.isStatus())
                    .build();
        }

        List<GradeSummaryDto> gradeDto = body.getGrades().stream()
                .map(dto -> GradeSummaryDto.builder()
                        .uuid(dto.getUuid())
                        .name(dto.getName())
                        .build())
                .toList();
        return StaffResponseDto.builder()
                .uuid(body.getUuid())
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .email(body.getEmail())
                .phoneNumber(body.getPhoneNumber())
                .gender(body.getGender())
                .role(staffRoleResponseDto)
                .employmentType(body.getEmploymentType())
                .isActive(body.getIsActive())
                .subjects(subjectDto)
                .grades(gradeDto)
                .dateOfBirth(body.getDateOfBirth())
                .idNumber(body.getIdNumber())
                .address(body.getAddress())
                .practiceNumber(body.getPracticeNumber())
                .staffType(body.getStaffType())
                .qualification(body.getQualification())
                .experience(body.getExperience())
                .joinedDate(body.getJoinedDate())
                .emergencyContactName(body.getEmergencyContactName())
                .emergencyContactPhone(body.getEmergencyContactPhone())
                .emergencyContactRelationship(body.getEmergencyContactRelationship())
                .documents(body.getDocuments())
                .build();
    }


    private List<SubjectEntity> resolveSubjects(List<UUID> uuids){
        if (uuids == null || uuids.isEmpty()) return new ArrayList<>();

        List<UUID> distinct = uuids.stream().distinct().toList();
        List<SubjectEntity> found = subjectRepo.findAllById(distinct);

        if (found.size() != distinct.size()) {
            Set<UUID> foundIds = found.stream().map(SubjectEntity::getUuid).collect(Collectors.toSet());
            List<UUID> missing = distinct.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject(s) not found: " + missing);
        }
        return new ArrayList<>(found);
    }

    private List<GradeEntity> resolveGrades(List<UUID> uuids){
        if (uuids == null || uuids.isEmpty()) return new ArrayList<>();

        List<UUID> distinct = uuids.stream().distinct().toList();
        List<GradeEntity> found = gradeRepo.findAllById(distinct);
        if (found.size() != distinct.size()) {
            Set<UUID> foundIds = found.stream().map(GradeEntity::getUuid).collect(Collectors.toSet());
            List<UUID> missing = distinct.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject(s) not found: " + missing);
        }
        return new ArrayList<>(found);
    }

    @Transactional
    public void create(StaffRequestDto req) {
        if (req.getEmail() != null && repo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("A Staff member with this email already exists");
        }

        if (req.getPhoneNumber() != null && repo.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new RuntimeException("A Staff member with this phone number already exists");
        }

        List<SubjectEntity> existingSubjects = resolveSubjects(req.getSubjectUuids());
        List<GradeEntity> existingGrades = resolveGrades(req.getGradeUuids());

        StaffRole staffRole = null;
        if (req.getRole() != null) {
            staffRole = staffRoleRepository.findByUuid(req.getRole())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff role not found"));
        }

        repo.save(StaffEntity.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .gender(req.getGender())
                .role(staffRole)
                .employmentType(req.getEmploymentType())
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .subjects(existingSubjects)
                .grades(existingGrades)
                .dateOfBirth(req.getDateOfBirth())
                .idNumber(req.getIdNumber())
                .address(req.getAddress())
                .practiceNumber(req.getPracticeNumber())
                .staffType(req.getStaffType())
                .qualification(req.getQualification())
                .experience(req.getExperience())
                .joinedDate(req.getJoinedDate() != null ? req.getJoinedDate() : LocalDate.now())
                .emergencyContactName(req.getEmergencyContactName())
                .emergencyContactPhone(req.getEmergencyContactPhone())
                .emergencyContactRelationship(req.getEmergencyContactRelationship())
                .documents(req.getDocuments() != null ? req.getDocuments() : new ArrayList<>())
                .build());
    }

    @Transactional
    public void update(UUID uuid, StaffRequestDto req) {
        StaffEntity existingStaff = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No staff member found with this uuid"));

        if (req.getEmail() != null && repo.existsByEmailAndUuidNot(req.getEmail(), uuid)) {
            throw new RuntimeException("A Staff member with this email already exists");
        }

        if (req.getPhoneNumber() != null && repo.existsByPhoneNumberAndUuidNot(req.getPhoneNumber(), uuid)) {
            throw new RuntimeException("A Staff member with this phone number already exists");
        }

        List<SubjectEntity> existingSubjects = resolveSubjects(req.getSubjectUuids());
        List<GradeEntity> existingGrades = resolveGrades(req.getGradeUuids());

        StaffRole staffRole = null;
        if (req.getRole() != null) {
            staffRole = staffRoleRepository.findByUuid(req.getRole())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff role not found"));
        }

        existingStaff.setFirstName(req.getFirstName());
        existingStaff.setLastName(req.getLastName());
        existingStaff.setEmail(req.getEmail());
        existingStaff.setPhoneNumber(req.getPhoneNumber());
        existingStaff.setGender(req.getGender());
        existingStaff.setRole(staffRole);
        existingStaff.setEmploymentType(req.getEmploymentType());
        existingStaff.setIsActive(req.getIsActive() != null ? req.getIsActive() : existingStaff.getIsActive());
        existingStaff.setSubjects(existingSubjects);
        existingStaff.setGrades(existingGrades);
        existingStaff.setDateOfBirth(req.getDateOfBirth());
        existingStaff.setIdNumber(req.getIdNumber());
        existingStaff.setAddress(req.getAddress());
        existingStaff.setPracticeNumber(req.getPracticeNumber());
        existingStaff.setStaffType(req.getStaffType());
        existingStaff.setQualification(req.getQualification());
        existingStaff.setExperience(req.getExperience());
        existingStaff.setJoinedDate(req.getJoinedDate());
        existingStaff.setEmergencyContactName(req.getEmergencyContactName());
        existingStaff.setEmergencyContactPhone(req.getEmergencyContactPhone());
        existingStaff.setEmergencyContactRelationship(req.getEmergencyContactRelationship());
        existingStaff.setDocuments(req.getDocuments() != null ? req.getDocuments() : new ArrayList<>());

        repo.save(existingStaff);
    }
    @Transactional
    public void toggleStatus(UUID uuid){
        StaffEntity existingStaff = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No staff member found with this uuid"));

        existingStaff.setIsActive(!existingStaff.getIsActive());
        repo.save(existingStaff);
    }

    @Transactional
    public void deleteStaff(UUID uuid){
        StaffEntity existingStaff = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No staff member found with this uuid"));
        repo.delete(existingStaff);
    }


}
