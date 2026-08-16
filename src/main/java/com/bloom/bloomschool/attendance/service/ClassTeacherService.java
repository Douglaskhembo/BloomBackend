package com.bloom.bloomschool.attendance.service;

import com.bloom.bloomschool.attendance.dto.request.ClassTeacherRequest;
import com.bloom.bloomschool.attendance.dto.response.ClassTeacherResponse;
import com.bloom.bloomschool.attendance.entity.ClassTeacherAssignment;
import com.bloom.bloomschool.attendance.repository.ClassTeacherAssignmentRepository;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.school.repository.GradeLevelRepository;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassTeacherService {

    private final ClassTeacherAssignmentRepository repo;
    private final StaffRepository staffRepository;
    private final StudentRepository studentRepository;
    private final GradeLevelRepository gradeLevelRepository;

    public List<ClassTeacherResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ClassTeacherResponse assign(ClassTeacherRequest req) {
        Staff teacher = staffRepository.findByUuid(req.getTeacherUuid())
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));
        GradeLevel gradeLevel = gradeLevelRepository.findByUuid(req.getGradeLevelUuid())
                .orElseThrow(() -> new EntityNotFoundException("Grade level not found"));

        Optional<ClassTeacherAssignment> existingForClass = repo.findByGradeLevel_UuidAndStream(req.getGradeLevelUuid(), req.getStream());
        if (existingForClass.isPresent() && !existingForClass.get().getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("Grade " + gradeLevel.getName() + " " + req.getStream() + " already has a class teacher");
        }

        repo.findByTeacherId(teacher.getId())
                .filter(a -> existingForClass.isEmpty() || !a.getId().equals(existingForClass.get().getId()))
                .ifPresent(repo::delete);

        ClassTeacherAssignment saved = existingForClass.orElseGet(ClassTeacherAssignment::new);
        saved.setTeacher(teacher);
        saved.setGradeLevel(gradeLevel);
        saved.setStream(req.getStream());
        return toResponse(repo.save(saved));
    }

    @Transactional
    public void unassign(UUID uuid) {
        ClassTeacherAssignment a = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));
        repo.deleteById(a.getId());
    }

    public ClassTeacherResponse getMine(UUID teacherUuid) {
        return repo.findByTeacherUuid(teacherUuid).map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("No class assigned to this teacher"));
    }

    public List<Student> getMyRoster(UUID teacherUuid) {
        ClassTeacherAssignment a = repo.findByTeacherUuid(teacherUuid)
                .orElseThrow(() -> new EntityNotFoundException("No class assigned to this teacher"));
        return studentRepository.findByGradeAndStreamAndStatus(a.getGradeLevel().getName(), a.getStream(), Student.Status.ACTIVE);
    }

    private ClassTeacherResponse toResponse(ClassTeacherAssignment a) {
        return ClassTeacherResponse.builder()
                .uuid(a.getUuid())
                .teacherUuid(a.getTeacher().getUuid())
                .teacherName(a.getTeacher().getFirstName() + " " + a.getTeacher().getLastName())
                .staffId(a.getTeacher().getStaffId())
                .gradeLevelUuid(a.getGradeLevel().getUuid())
                .grade(a.getGradeLevel().getName())
                .stream(a.getStream())
                .build();
    }
}
