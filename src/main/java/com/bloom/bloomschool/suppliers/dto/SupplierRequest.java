package com.bloom.bloomschool.suppliers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class SupplierRequest {
    @NotBlank private String name;
    private Long id;
    private UUID uuid;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String category;
    private String kraPin;
}
