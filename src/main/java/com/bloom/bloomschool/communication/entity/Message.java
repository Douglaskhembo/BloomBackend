package com.bloom.bloomschool.communication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bloom_school_message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, updatable = false)
    private Long message_id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid(){
        if(uuid == null) uuid = UUID.randomUUID();
    }

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private String gradeFilter;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String recipientCount;

    @Enumerated(EnumType.STRING)
    private ChannelType channelType;

    @Enumerated(EnumType.STRING)
    private AudienceType audienceType;
}
//Recipient