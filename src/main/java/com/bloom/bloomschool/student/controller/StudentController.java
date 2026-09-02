package com.bloom.bloomschool.student.controller;

import com.bloom.bloomschool.student.dto.StudentRequestDTO;
import com.bloom.bloomschool.student.dto.StudentResponseDTO;
import com.bloom.bloomschool.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // Get all students
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // Get single student by UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<StudentResponseDTO> getStudentByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(studentService.getStudentByUuid(uuid));
    }

    // Create a student
    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody StudentRequestDTO request) {
        studentService.createStudent(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Update student data
    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody StudentRequestDTO req) {
        studentService.updateStudent(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // Delete student
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID uuid) {
        studentService.deleteStudent(uuid);
        return ResponseEntity.noContent().build();
    }
}