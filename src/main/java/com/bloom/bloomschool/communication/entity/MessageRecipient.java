package com.bloom.bloomschool.communication.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_message_recipients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageRecipient extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipientType recipientType;

    private String recipientName;

    @Column(nullable = false)
    private String recipientEmail;

    @Builder.Default
    private boolean read = false;

    private LocalDateTime readAt;

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    public enum RecipientType { PARENT, TEACHER, STAFF }
}
