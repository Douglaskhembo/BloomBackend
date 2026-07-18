package com.bloom.bloomschool.transport.dto;

import com.bloom.bloomschool.transport.entity.Route;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RouteRequest {
    @NotBlank private String name;
    private String driver;
    private String driverPhone;
    private String vehicle;
    private int capacity;
    private double fare;
    private List<String> pickupPoints;
    private Route.Status status;
}
