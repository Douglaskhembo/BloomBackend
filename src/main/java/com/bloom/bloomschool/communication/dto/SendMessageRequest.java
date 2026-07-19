package com.bloom.bloomschool.communication.dto;

import com.bloom.bloomschool.communication.entity.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotBlank private String subject;
    @NotBlank private String body;
    private Message.ChannelType channel;
    @NotNull private Message.AudienceType audience;
    private String gradeFilter;
}
