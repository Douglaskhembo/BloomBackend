package com.bloom.bloomschool.communication.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Message extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 4000)
    private String body;

    /** Delivery channel is recorded for reference only — only IN_APP is actually delivered
     * (no SMS/WhatsApp/email gateway is configured in this project). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ChannelType channel = ChannelType.IN_APP;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AudienceType audience;

    /** Only set when audience = PARENTS_BY_GRADE. */
    private String gradeFilter;

    private String senderName;

    private int recipientCount;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    public enum ChannelType { IN_APP, SMS, WHATSAPP, EMAIL }
    public enum AudienceType { ALL_PARENTS, ALL_TEACHERS, ALL_STAFF, PARENTS_BY_GRADE }
}
