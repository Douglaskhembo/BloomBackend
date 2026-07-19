package com.bloom.bloomschool.attendance.service;

import com.bloom.bloomschool.attendance.dto.request.ClassTeacherRequest;
import com.bloom.bloomschool.attendance.dto.response.ClassTeacherResponse;
import com.bloom.bloomschool.attendance.entity.ClassTeacherAssignment;
import com.bloom.bloomschool.attendance.repository.ClassTeacherAssignmentRepository;
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

    public List<ClassTeacherResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    /** One teacher : one grade+stream. Reassigning a teacher to a new class moves them off their old one. */
    @Transactional
    public ClassTeacherResponse assign(ClassTeacherRequest req) {
        Staff teacher = staffRepository.findByUuid(req.getTeacherUuid())
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        Optional<ClassTeacherAssignment> existingForClass = repo.findByGradeAndStream(req.getGrade(), req.getStream());
        if (existingForClass.isPresent() && !existingForClass.get().getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("Grade " + req.getGrade() + " " + req.getStream() + " already has a class teacher");
        }

        repo.findByTeacherId(teacher.getId())
                .filter(a -> existingForClass.isEmpty() || !a.getId().equals(existingForClass.get().getId()))
                .ifPresent(repo::delete);

        ClassTeacherAssignment saved = existingForClass.orElseGet(ClassTeacherAssignment::new);
        saved.setTeacher(teacher);
        saved.setGrade(req.getGrade());
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
        return studentRepository.findByGradeAndStreamAndStatus(a.getGrade(), a.getStream(), Student.Status.ACTIVE);
    }

    private ClassTeacherResponse toResponse(ClassTeacherAssignment a) {
        return ClassTeacherResponse.builder()
                .uuid(a.getUuid())
                .teacherUuid(a.getTeacher().getUuid())
                .teacherName(a.getTeacher().getFirstName() + " " + a.getTeacher().getLastName())
                .staffId(a.getTeacher().getStaffId())
                .grade(a.getGrade())
                .stream(a.getStream())
                .build();
    }
}
