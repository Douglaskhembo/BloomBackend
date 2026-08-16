package com.bloom.bloomschool.students.repository;

import com.bloom.bloomschool.students.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUuid(UUID uuid);
    Optional<Student> findByAdmissionNumber(String admissionNumber);
    boolean existsByAdmissionNumber(String admissionNumber);
    List<Student> findByGrade(String grade);
    List<Student> findByGradeAndStatus(String grade, Student.Status status);
    List<Student> findByParentUserUuidAndStatus(UUID parentUserUuid, Student.Status status);
    long countByGradeAndStatus(String grade, Student.Status status);
    long count();

    /** Streamless-grade tolerant: TimetableEntry/ClassTeacherAssignment always normalize "no
     *  stream" to "" (never null), but a Student enrolled into a streamless grade may still have
     *  `stream` stored as null (e.g. rows created before that normalization existed, or via a path
     *  that never set it) — a plain `=` match would then silently return zero students for a grade
     *  that plainly has them. Treating "" and null as the same "no stream" value on both sides of
     *  the comparison fixes that without needing a data migration; a real stream value (e.g. "A")
     *  still matches exactly and never bleeds into another stream. */
    @Query("SELECT s FROM Student s WHERE s.grade = :grade AND s.status = :status AND " +
            "(((:stream IS NULL OR :stream = '') AND (s.stream IS NULL OR s.stream = '')) OR s.stream = :stream)")
    List<Student> findByGradeAndStreamAndStatus(String grade, String stream, Student.Status status);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.grade = :grade AND s.status = :status AND " +
            "(((:stream IS NULL OR :stream = '') AND (s.stream IS NULL OR s.stream = '')) OR s.stream = :stream)")
    long countByGradeAndStreamAndStatus(String grade, String stream, Student.Status status);

    /** Column-only projection used to compute the next admission number — see
     *  StudentService.generateAdmissionNumber. */
    @Query("SELECT s.admissionNumber FROM Student s")
    List<String> findAllAdmissionNumbers();

    @Query("SELECT s FROM Student s WHERE LOWER(CONCAT(s.firstName,' ',s.lastName,' ',s.admissionNumber)) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Student> search(String q);

    @Query("SELECT s FROM Student s WHERE s.status = 'ACTIVE' AND (:grade IS NULL OR s.grade = :grade) AND (:stream IS NULL OR s.stream = :stream)")
    List<Student> findActiveRoster(String grade, String stream);

    /** Every status, not just ACTIVE — fee arrears/collection reports must still surface a
     *  SUSPENDED student's real outstanding balance (suspension is sometimes itself a
     *  consequence of non-payment), not silently drop them from the report. */
    @Query("SELECT s FROM Student s WHERE (:grade IS NULL OR s.grade = :grade) AND (:stream IS NULL OR s.stream = :stream)")
    List<Student> findRoster(String grade, String stream);
}
