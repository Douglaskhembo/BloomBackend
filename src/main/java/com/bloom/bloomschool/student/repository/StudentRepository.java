package com.bloom.bloomschool.student.repository;

import com.bloom.bloomschool.student.entity.Student;
import com.bloom.bloomschool.student.enums.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{
    Optional<Student> findByUuid(UUID uuid);

    Optional<Student> findByAdmissionNumber(String admissionNumber);

    @Query(value = "SELECT * FROM bloom_school_student st WHERE st.uuid = :uuid", nativeQuery = true)
    Optional<Student> findByUuid(@Param("uuid") UUID uuid);

    List<Student> findAll();

    @Query("SELECT COUNT(st) FROM Student st WHERE st.gradeEntity.uuid = :gradeUuid AND st.stream = :stream AND st.status = :status")
    int getStreamCapacity(@Param("gradeUuid") UUID gradeUuid,
                                  @Param("stream") String stream,
                                  @Param("status") StudentStatus studentStatus);

    @Query("SELECT COUNT(st) FROM Student st WHERE st.gradeEntity.uuid = :gradeUuid AND st.status = :status")
    int getGradeCapacity(@Param("gradeUuid") UUID gradeUuid,
                                 @Param("status") StudentStatus studentStatus);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM bloom_school_student st WHERE st.uuid = :uuid
            """, nativeQuery = true)
    boolean existsByUuid(@Param("uuid") UUID uuid);

    boolean existsByEntryNumber(String entryNumber);

    boolean existsByEntryNumberAndUuidNot(String entryNumber, UUID uuid);

    @Query("SELECT s.document_type_uuid FROM Student s WHERE s.uuid =:StudentUuid")
    List<UUID> getDocumentUuids(@Param("StudentUuid") UUID studentUuid);

    boolean existsByUuid(UUID uuid);
}
