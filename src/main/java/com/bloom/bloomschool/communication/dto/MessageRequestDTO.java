package com.bloom.bloomschool.communication.dto;

import lombok.*;
import com.bloom.bloomschool.communication.entity.AudienceType;
import com.bloom.bloomschool.communication.entity.ChannelType;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MessageRequestDTO {

    private String subject;

    private String body;

    private String gradeFilter;

    private String senderName;

    private String recipientCount;

    private ChannelType channelType;

    private AudienceType audienceType;


}
