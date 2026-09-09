package com.bloom.bloomschool.assessment.service;

import com.bloom.bloomschool.assessment.dto.AssessmentDto;
import com.bloom.bloomschool.assessment.dto.AssessmentMarksDto;
import com.bloom.bloomschool.assessment.dto.MarkEntryRequestDto;
import com.bloom.bloomschool.assessment.entity.Assessment;
import com.bloom.bloomschool.assessment.entity.AssessmentMarks;
import com.bloom.bloomschool.assessment.repository.AssessmentMarkRepository;
import com.bloom.bloomschool.assessment.repository.AssessmentRepository;
import com.bloom.bloomschool.gradeLevel.dto.GradeDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.gradeLevel.repository.GradeRepository;
import com.bloom.bloomschool.staff.dto.StaffDto;
import com.bloom.bloomschool.staff.entity.StaffEntity;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.student.dto.StudentRequestDTO;
import com.bloom.bloomschool.student.entity.Student;
import com.bloom.bloomschool.student.repository.StudentRepository;
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

    public List<AssessmentMarksDto> getMarks(){
        List<AssessmentMarks> assessmentMarks = assessmentMarkRepo.findAll();
        if(CollectionUtils.isEmpty(assessmentMarks)){return List.of();}
        return assessmentMarks.stream().map(this::convertMarksToDto).toList();
    }

    private AssessmentMarksDto convertMarksToDto(AssessmentMarks body){
        Student student = body.getStudent();
        StudentRequestDTO studentRequestDTO = StudentRequestDTO.builder()
                .studentUuid(student.getUuid())
                .admissionNumber(student.getAdmissionNumber())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .build();


        return AssessmentMarksDto.builder().score(body.getScore())
                .studentRequestDTO(studentRequestDTO)
                .build();
    }


    @Transactional
    public void saveMarks(UUID assesmentUuid, MarkEntryRequestDto req){
        Assessment assessment = repo.findByUuid(assesmentUuid)
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
    }

    public List<AssessmentDto> getAll(){
        List<Assessment> assessment = repo.findAll();
        if (CollectionUtils.isEmpty(assessment)){
            return List.of();
        }
        return assessment.stream().map(this::convertToDto).toList();
    }

    public AssessmentDto getAssessment(UUID uuid) throws BadRequestException{
        return convertToDto(repo.findByUuid(uuid)
                .orElseThrow(() -> new BadRequestException("No Assessment found")));
    }

    private AssessmentDto convertToDto(Assessment body){
        GradeEntity gradeEntity = body.getGrade();
        GradeDto gradeDto = GradeDto.builder()
                .name(gradeEntity.getName())
                .displayOrder(gradeEntity.getDisplayOrder())
                .capacity(gradeEntity.getCapacity())
                .isActive(gradeEntity.getIsActive())
                .streamName(gradeEntity.getStreamName())
                .streamCapacity(gradeEntity.getStreamCapacity())
                .streams(gradeEntity.getStreams()).build();

        SubjectEntity subjectEntity = body.getSubject();
        SubjectDto subjectDto = SubjectDto.builder()
                .name(subjectEntity.getName())
                .subjectCode(subjectEntity.getSubjectCode())
                .isActive(subjectEntity.getIsActive())
                .build();

        StaffEntity staffEntity = body.getStaff();
        StaffDto staffDto = StaffDto.builder()
                .firstName(staffEntity.getFirstName())
                .lastName(staffEntity.getLastName())
                .email(staffEntity.getEmail())
                .phoneNumber(staffEntity.getPhoneNumber())
                .gender(staffEntity.getGender())
                .role(staffEntity.getRole())
                .employmentType(staffEntity.getEmploymentType())
                .isActive(staffEntity.getIsActive())
                .build();

        return AssessmentDto.builder()
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

    public void createAssessment(AssessmentDto req){
        repo.save(convertToEntity(req));
    }
    public void updateAssessment(UUID uuid, AssessmentDto req){
        Assessment existingAssessment = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found"));

        existingAssessment.setName(req.getName());
        existingAssessment.setTerm(req.getTerm());
        existingAssessment.setMaxScore(req.getMaxScore());
        existingAssessment.setYear(req.getYear());
        existingAssessment.setStream(req.getStream());
        existingAssessment.setAssessmentType(req.getAssessmentType());
    }
    private Assessment convertToEntity(AssessmentDto body){
        GradeEntity grade = gradeRepo.findByUuid(body.getGradeUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));

        StaffEntity staff = staffRepo.findByUuid(body.getStaffUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        SubjectEntity subject = subjectRepo.findByUuid(body.getSubjectUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
        return Assessment.builder()
                .name(body.getName())
                .term(body.getTerm())
                .maxScore(body.getMaxScore())
                .year(body.getYear())
                .stream(body.getStream())
                .assessmentType(body.getAssessmentType())
                .grade(grade)
                .subject(subject)
                .staff(staff)
                .build();
    }

    public void deleteAssessment(UUID uuid){
        Assessment existingAssessment = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found"));
        repo.delete(existingAssessment);
    }

}
