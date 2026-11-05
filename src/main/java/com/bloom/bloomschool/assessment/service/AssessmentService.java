package com.bloom.bloomschool.assessment.service;

import com.bloom.bloomschool.assessment.dto.*;
import com.bloom.bloomschool.assessment.entity.Assessment;
import com.bloom.bloomschool.assessment.entity.AssessmentMarks;
import com.bloom.bloomschool.assessment.repository.AssessmentMarkRepository;
import com.bloom.bloomschool.assessment.repository.AssessmentRepository;
import com.bloom.bloomschool.gradeLevel.dto.GradeSummaryDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.gradeLevel.repository.GradeRepository;
import com.bloom.bloomschool.staff.dto.StaffSummaryDto;
import com.bloom.bloomschool.staff.entity.StaffEntity;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.student.dto.StudentSummaryDto;
import com.bloom.bloomschool.student.dto.SubjectSummaryDto;
import com.bloom.bloomschool.student.entity.Student;
import com.bloom.bloomschool.student.repository.StudentRepository;
import com.bloom.bloomschool.subject.entity.SubjectEntity;
import com.bloom.bloomschool.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AssessmentService {
    private final AssessmentRepository repo;
    private final GradeRepository gradeRepo;
    private final StaffRepository staffRepo;
    private final SubjectRepository subjectRepo;
    private final AssessmentMarkRepository assessmentMarkRepo;
    private final StudentRepository studentRepo;
    @Transactional(readOnly = true)
    public List<AssessmentMarkResponseDto> getMarks(){
        List<AssessmentMarks> assessmentMarks = assessmentMarkRepo.findAll();
        if(CollectionUtils.isEmpty(assessmentMarks)){return List.of();}
        return assessmentMarks.stream().map(this::convertMarksToDto).toList();
    }

    private AssessmentMarkResponseDto convertMarksToDto(AssessmentMarks body){
        Student student = body.getStudent();
        StudentSummaryDto studentRequestDTO = StudentSummaryDto.builder()
                .uuid(student.getUuid())
                .admissionNumber(student.getAdmissionNumber())
                .fullName(student.getFirstName() + " " + student.getLastName())
                .build();

        return AssessmentMarkResponseDto.builder()
                .score(body.getScore())
                .assessmentUuid(body.getAssessment().getUuid())
                .assessmentName(body.getAssessment().getName())
                .student(studentRequestDTO)
                .build();
    }


    @Transactional
    public void saveMarks(UUID assessmentUuid, MarkEntryRequestDto req){
        Assessment assessment = repo.findByUuid(assessmentUuid)
                .orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found"));
         int maxScore = assessment.getMaxScore();

        for(MarkEntryRequestDto.Entry entry : req.getEntries()){
            Student student = studentRepo.findByUuid(entry.getStudentUuid())
                    .orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
            if(entry.getScore() == null || entry.getScore() < 0 || entry.getScore() > maxScore){
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Score is not valid");
            }
            AssessmentMarks entity = new AssessmentMarks();
            entity.setScore(entry.getScore());
            entity.setStudent(student);
            entity.setAssessment(assessment);
            assessmentMarkRepo.save(entity);
        }


        for(MarkEntryRequestDto.Entry entry : req.getEntries()){
            Student student = studentRepo.findByUuid(entry.getStudentUuid())
                    .orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
            if(entry.getScore() == null || entry.getScore() < 0 || entry.getScore() > maxScore){
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Score is not valid");
            }
            AssessmentMarks mark = assessmentMarkRepo
                    .findByAssessmentUuidAndStudentUuid(assessmentUuid, entry.getStudentUuid())
                    .orElseGet(AssessmentMarks::new);

            mark.setScore(entry.getScore());
            mark.setStudent(student);
            mark.setAssessment(assessment);

            assessmentMarkRepo.save(mark);
        }
    }
    @Transactional(readOnly = true)
    public List<AssessmentResponseDto> getAll(){
        List<Assessment> assessment = repo.findAll();
        if (CollectionUtils.isEmpty(assessment)){
            return List.of();
        }
        return assessment.stream().map(this::convertToDto).toList();
    }
    @Transactional(readOnly = true)
    public AssessmentResponseDto getAssessment(UUID uuid){
        return convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No Assessment found")));
    }

    private AssessmentResponseDto convertToDto(Assessment body){
        GradeEntity gradeEntity = body.getGrade();
        GradeSummaryDto gradeDto = GradeSummaryDto.builder()
                .uuid(gradeEntity.getUuid())
                .name(gradeEntity.getName()).build();

        SubjectEntity subjectEntity = body.getSubject();
        SubjectSummaryDto subjectDto = SubjectSummaryDto.builder()
                .name(subjectEntity.getName())
                .subjectCode(subjectEntity.getSubjectCode())
                .isActive(subjectEntity.getIsActive())
                .build();

        StaffEntity staffEntity = body.getStaff();
        StaffSummaryDto staffDto = StaffSummaryDto.builder()
                .uuid(staffEntity.getUuid())
                .fullName(staffEntity.getFirstName()+" "+ staffEntity.getLastName())
                .email(staffEntity.getEmail())
                .build();

        return AssessmentResponseDto.builder()
                .name(body.getName())
                .term(body.getTerm())
                .maxScore(body.getMaxScore())
                .year(body.getYear())
                .stream(body.getStream())
                .assessmentType(body.getAssessmentType())
                .grade(gradeDto)
                .subject(subjectDto)
                .staff(staffDto)
                .build();
    }

    @Transactional
    public void createAssessment(AssessmentRequestDto req){
        if (repo.existsByNameAndTermAndYearAndSubjectUuidAndGradeUuid(req.getName(), req.getTerm(), req.getYear(),
                req.getSubjectUuid(), req.getGradeUuid())){
            throw new RuntimeException("An assessment with the same name, term, year, subject and grade already exists");
        }
        repo.save(convertToEntity(req));
    }

    @Transactional
    public void updateAssessment(UUID uuid, AssessmentRequestDto req){
        Assessment existingAssessment = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found"));

        if (repo.existsByNameAndTermAndYearAndSubjectUuidAndGradeUuidAndUuidNot(
                req.getName(), req.getTerm(), req.getYear(),
                req.getSubjectUuid(), req.getGradeUuid(), uuid)) {
            throw new RuntimeException(
                    "An assessment with the same name, term, year, subject and grade already exists");
        }

        GradeEntity grade = gradeRepo.findByUuid(req.getGradeUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));

        StaffEntity staff = staffRepo.findByUuid(req.getStaffUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        SubjectEntity subject = subjectRepo.findByUuid(req.getSubjectUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));

        existingAssessment.setName(req.getName());
        existingAssessment.setTerm(req.getTerm());
        existingAssessment.setMaxScore(req.getMaxScore());
        existingAssessment.setYear(req.getYear());
        existingAssessment.setStream(req.getStream());
        existingAssessment.setAssessmentType(req.getAssessmentType());
        existingAssessment.setGrade(grade);
        existingAssessment.setSubject(subject);
        existingAssessment.setStaff(staff);

        repo.save(existingAssessment);
    }
    private Assessment convertToEntity(AssessmentRequestDto body){
        GradeEntity grade = gradeRepo.findByUuid(body.getGradeUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));

        StaffEntity staff = staffRepo.findByUuid(body.getStaffUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        SubjectEntity subject = subjectRepo.findByUuid(body.getSubjectUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
        return Assessment.builder()
                .name(body.getName())
                .term(body.getTerm())
                .maxScore(body.getMaxScore() != null ? body.getMaxScore() : 100)                .year(body.getYear())
                .year(body.getYear())
                .stream(body.getStream())
                .assessmentType(body.getAssessmentType())
                .grade(grade)
                .subject(subject)
                .staff(staff)
                .build();
    }

    @Transactional
    public void deleteAssessment(UUID uuid){
        Assessment existingAssessment = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found"));
        repo.delete(existingAssessment);
    }

}
