package com.bloom.bloomschool.gradeLevel.dto;


import lombok.*;

import java.util.List;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeRequestDto {
     private String name;
     private Integer displayOrder;
     private Integer capacity;
    private Boolean isActive;
    private List<String> streamName;
    private List<Integer> streamCapacity;
    private Integer streams;
}