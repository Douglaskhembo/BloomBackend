package com.bloom.bloomschool.communication.service;

import com.bloom.bloomschool.communication.dto.MessageRequestDTO;
import com.bloom.bloomschool.communication.dto.MessageResponseDTO;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.communication.entity.Message;
import com.bloom.bloomschool.communication.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.*;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {
    private final MessageRepository messageRepo;

    private Message findEntity(UUID uuid){
        return messageRepo.findByUuid(uuid)
                .orElseThrow(() ->new ResourceNotFoundException("Message is Unavailable"));
    }

    public List<MessageResponseDTO> getAllMessages(){
        return messageRepo.findAll().stream()
                .map(m -> MessageResponseDTO.builder()
                        .uuid(m.getUuid())
                        .subject(m.getSubject())
                        .body(m.getBody())
                        .gradeFilter(m.getGradeFilter())
                        .senderName(m.getSenderName())
                        .recipientCount(m.getRecipientCount())
                        .audienceType(m.getAudienceType())
                        .channelType(m.getChannelType())
                        .build())
                .toList();
    }
    public MessageResponseDTO getMessageByUuid(UUID uuid){
        Message m = findEntity(uuid);

        return MessageResponseDTO.builder()
                .uuid(m.getUuid())
                .subject(m.getSubject())
                .body(m.getBody())
                .gradeFilter(m.getGradeFilter())
                .senderName(m.getSenderName())
                .recipientCount(m.getRecipientCount())
                .audienceType(m.getAudienceType())
                .channelType(m.getChannelType())
                .build();
    }
    @Transactional
    public void sendMessage(MessageRequestDTO req){
        //Link to user
        messageRepo.save(Message.builder()
                .subject(req.getSubject())
                .body(req.getBody())
                .gradeFilter(req.getGradeFilter())
                .senderName(req.getSenderName())
                .recipientCount(req.getRecipientCount())
                .audienceType(req.getAudienceType())
                .channelType(req.getChannelType())
                .build());
    }
    @Transactional
    public void updateMessage(UUID uuid, MessageRequestDTO req){
        Message m = findEntity(uuid);

        m.setSubject(req.getSubject());
        m.setBody(req.getBody());
        m.setGradeFilter(req.getGradeFilter());
        m.setSenderName(req.getSenderName());
        m.setRecipientCount(req.getRecipientCount());
        m.setAudienceType(req.getAudienceType());
        m.setChannelType(req.getChannelType());

        messageRepo.save(m);
    }
    @Transactional
    public void deleteMessage(UUID uuid){
        Message m = findEntity(uuid);
        messageRepo.delete(m);
    }
}
//Spring Security
