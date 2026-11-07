package com.bloom.bloomschool.gradeLevel.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeResponseDto {
    private UUID uuid;
    private String name;
    private int displayOrder;
    private Integer capacity;
    private boolean isActive;
    private List<String> streamName;
    private List<Integer> streamCapacity;
    private int streams;
}
