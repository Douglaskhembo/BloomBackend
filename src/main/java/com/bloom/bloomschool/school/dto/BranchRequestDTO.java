package com.bloom.bloomschool.school.dto;

import com.bloom.bloomschool.school.entity.Department;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BranchRequestDTO {
    private String branchName;
    private String branchCode;
    private String phone;
    private String location;
    private List<UUID> departmentsUuid;


}
