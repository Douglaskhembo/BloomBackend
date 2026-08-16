package com.bloom.bloomschool.assessments.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyClassResponse {
    private UUID gradeLevelUuid;
    private String grade;
    private String stream;
    private UUID subjectUuid;
    private String subjectName;
    private long studentCount;
    /** Only populated by the unscoped "all classes" listing (admin/class-teacher browsing);
     *  null on the teacher-scoped "my classes" listing since the caller already knows who "my" is. */
    private UUID teacherUuid;
    private String teacherName;
}
