package com.bloom.bloomschool.communication.dto;

import lombok.*;
import java.util.UUID;
import com.bloom.bloomschool.communication.entity.AudienceType;
import com.bloom.bloomschool.communication.entity.ChannelType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class MessageResponseDTO {
    private UUID uuid;

    private String subject;

    private String body;

    private String gradeFilter;

    private String senderName;

    private String recipientCount;

    private ChannelType channelType;

    private AudienceType audienceType;
}
