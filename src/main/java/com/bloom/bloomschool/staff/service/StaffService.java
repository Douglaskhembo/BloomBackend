package com.bloom.bloomschool.staff.service;

import com.bloom.bloomschool.staff.dto.StaffDto;
import com.bloom.bloomschool.staff.entity.StaffEntity;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.subject.dto.SubjectDto;
import com.bloom.bloomschool.subject.entity.SubjectEntity;
import com.bloom.bloomschool.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

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

    @Transactional
    public List<StaffDto> getAll(){
        List<StaffEntity> staffEntity = repo.findAll();
        if (CollectionUtils.isEmpty(staffEntity)){
            return List.of();
        }
        return staffEntity.stream()
                .map(this::convertToDto).toList();
    }

    @Transactional
    public StaffDto getByUuid(UUID uuid) {
        return convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No staff found")));
    }

    private StaffDto convertToDto(StaffEntity body){
        List<SubjectDto> subjectDto = body.getSubjects().stream()
                .map(dto -> SubjectDto.builder()
                        .name(dto.getName())
                        .subjectCode(dto.getSubjectCode())
                        .status(dto.getIsActive())
                                .build())
                .toList();
        return StaffDto.builder()
                .uuid(body.getUuid())
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .email(body.getEmail())
                .phoneNumber(body.getPhoneNumber())
                .gender(body.getGender())
                .role(body.getRole())
                .employmentType(body.getEmploymentType())
                .isActive(body.getIsActive())
                .subjects(subjectDto)
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

    @Transactional
    public void create(StaffDto req){
        if (repo.existsByEmail(req.getEmail())){
            throw new RuntimeException("A Staff member with this email already exists");
        }

        if (req.getPhoneNumber() != null && repo.existsByPhoneNumber(req.getPhoneNumber())){
            throw new RuntimeException("A Staff member with this phone number already exists");
        }
        List<SubjectEntity> existingSubjects = resolveSubjects(req.getSubjectUuids());
        repo.save(StaffEntity.builder()
            .firstName(req.getFirstName())
            .lastName(req.getLastName())
            .email(req.getEmail())
            .phoneNumber(req.getPhoneNumber())
            .gender(req.getGender())
            .role(req.getRole())
            .employmentType((req.getEmploymentType()))
            .isActive(req.getIsActive())
            .subjects(existingSubjects)
            .isActive(req.getIsActive() != null ? req.getIsActive() : true)
            .build());

    }

    @Transactional
    public void update( UUID uuid,StaffDto req){
        StaffEntity existingStaff = repo.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("No staff member found with this uuid"));
        
        if (repo.existsByEmailAndUuidNot(req.getEmail(), uuid)){
            throw new RuntimeException("A Staff member with this email already exists");
        }

        if (req.getPhoneNumber() != null && repo.existsByPhoneNumberAndUuidNot(req.getPhoneNumber(), uuid)){
            throw new RuntimeException("A Staff member with this phone number already exists");
        }
        List<SubjectEntity> existingSubjects = resolveSubjects(req.getSubjectUuids());

        existingStaff.setFirstName(req.getFirstName());
        existingStaff.setLastName(req.getLastName());
        existingStaff.setEmail(req.getEmail());
        existingStaff.setPhoneNumber(req.getPhoneNumber());
        existingStaff.setGender(req.getGender());
        existingStaff.setRole(req.getRole());
        existingStaff.setEmploymentType(req.getEmploymentType());
        existingStaff.setIsActive(req.getIsActive() != null ? req.getIsActive() : existingStaff.getIsActive());
        existingStaff.setSubjects(existingSubjects);
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
