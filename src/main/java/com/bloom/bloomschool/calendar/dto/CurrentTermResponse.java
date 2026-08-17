package com.bloom.bloomschool.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentTermResponse {
    private Integer academicYear;
    private String term;
}
