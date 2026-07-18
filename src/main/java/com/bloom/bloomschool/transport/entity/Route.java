package com.bloom.bloomschool.transport.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_transport_routes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Route extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private String name;

    private String driver;
    private String driverPhone;
    private String vehicle;
    private int capacity;
    private double fare;

    @ElementCollection
    @CollectionTable(name = "bloom_sch_route_pickup_points", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "pickup_point")
    @Builder.Default
    private List<String> pickupPoints = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    public enum Status { ACTIVE, MAINTENANCE, INACTIVE }
}
