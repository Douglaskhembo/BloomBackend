package com.bloom.bloomschool.transport.dto;

import com.bloom.bloomschool.transport.entity.Route;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RouteRequest {
    @NotBlank private String name;
    @NotNull private UUID driverUuid;
    private String vehicle;
    private int capacity;
    private double fare;
    private List<String> pickupPoints;
    private Route.Status status;
}
