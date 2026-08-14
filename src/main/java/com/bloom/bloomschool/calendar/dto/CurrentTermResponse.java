package com.bloom.bloomschool.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** academicYear/term are both null when today doesn't fall inside any configured term period —
 *  callers must treat that as "no automatic default available", not an error. */
@Data
@AllArgsConstructor
public class CurrentTermResponse {
    private Integer academicYear;
    private String term;
}
