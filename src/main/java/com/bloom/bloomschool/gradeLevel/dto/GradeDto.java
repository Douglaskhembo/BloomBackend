package com.bloom.bloomschool.gradeLevel.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class GradeDto {
    private String name;
    private Integer displayOrder;
    private Integer capacity;
    private boolean active;
    private List<String> streamName;
    private List<Integer> streamCapacity;
    private int streams = 1;
    private List<UUID> subjectUuids;
    private UUID uuid;
}
