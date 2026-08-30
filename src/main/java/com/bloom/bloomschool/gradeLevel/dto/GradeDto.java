package com.bloom.bloomschool.gradeLevel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDto {
    private String name;
    private int displayOrder;
    private int capacity;
    private Boolean isActive;
    private List<String> streamName;
    private List<Integer> streamCapacity;
    private int streams;
    private List<UUID> subjectUuids;
}
