package com.bloom.bloomschool.communication.service;

import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.auth.repo.UserRepository;
import com.bloom.bloomschool.auth.service.AuthService;
import com.bloom.bloomschool.communication.dto.SendMessageRequest;
import com.bloom.bloomschool.communication.entity.Message;
import com.bloom.bloomschool.communication.entity.MessageRecipient;
import com.bloom.bloomschool.communication.repository.MessageRecipientRepository;
import com.bloom.bloomschool.communication.repository.MessageRepository;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.staff.util.StaffType;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Only IN_APP delivery is real — recipients see messages in their in-app inbox. SMS/WhatsApp/
 * Email are recorded as the intended channel but not actually dispatched anywhere, since no
 * gateway (Africa's Talking, Twilio, SMTP, etc.) is configured in this project.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunicationService {

    private final MessageRepository messageRepo;
    private final MessageRecipientRepository recipientRepo;
    private final StaffRepository staffRepo;
    private final StudentRepository studentRepo;
    private final UserRepository userRepository;
    private final AuthService authService;

    public List<Message> getAllMessages() {
        return messageRepo.findAllByOrderBySentAtDesc();
    }

    @Transactional
    public Message sendMessage(SendMessageRequest req) {
        User sender = currentUser();
        List<MessageRecipient> recipients = resolveRecipients(req.getAudience(), req.getGradeFilter());
        LocalDateTime now = LocalDateTime.now();

        Message message = messageRepo.save(Message.builder()
                .subject(req.getSubject())
                .body(req.getBody())
                .channel(req.getChannel() != null ? req.getChannel() : Message.ChannelType.IN_APP)
                .audience(req.getAudience())
                .gradeFilter(req.getGradeFilter())
                .senderName(displayName(sender))
                .recipientCount(recipients.size())
                .sentAt(now)
                .build());

        for (MessageRecipient r : recipients) {
            r.setMessage(message);
            r.setReceivedAt(now);
        }
        recipientRepo.saveAll(recipients);
        return message;
    }

    public List<MessageRecipient> getInbox() {
        return recipientRepo.findByRecipientEmailOrderByReceivedAtDesc(currentUser().getEmail());
    }

    public long getUnreadCount() {
        return recipientRepo.countByRecipientEmailAndReadFalse(currentUser().getEmail());
    }

    @Transactional
    public void markRead(UUID recipientUuid) {
        MessageRecipient r = recipientRepo.findByUuid(recipientUuid)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        r.setRead(true);
        r.setReadAt(LocalDateTime.now());
        recipientRepo.save(r);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private List<MessageRecipient> resolveRecipients(Message.AudienceType audience, String gradeFilter) {
        return switch (audience) {
            case ALL_TEACHERS -> staffRepo.findByStaffType(StaffType.TEACHING).stream()
                    .filter(s -> s.getEmail() != null && !s.getEmail().isBlank())
                    .map(s -> MessageRecipient.builder()
                            .recipientType(MessageRecipient.RecipientType.TEACHER)
                            .recipientName(s.getFirstName() + " " + s.getLastName())
                            .recipientEmail(s.getEmail())
                            .build())
                    .toList();
            case ALL_STAFF -> staffRepo.findAll().stream()
                    .filter(s -> s.getEmail() != null && !s.getEmail().isBlank())
                    .map(this::toStaffRecipient)
                    .toList();
            case ALL_PARENTS, PARENTS_BY_GRADE -> {
                List<Student> students = (audience == Message.AudienceType.PARENTS_BY_GRADE && gradeFilter != null && !gradeFilter.isBlank())
                        ? studentRepo.findByGrade(gradeFilter)
                        : studentRepo.findAll();
                // Dedup by parent email — a student's parent may appear on multiple records, but should get one message.
                Map<String, String> byEmail = new LinkedHashMap<>();
                for (Student s : students) {
                    if (s.getParentEmail() != null && !s.getParentEmail().isBlank())
                        byEmail.putIfAbsent(s.getParentEmail(), s.getParentName());
                }
                yield byEmail.entrySet().stream()
                        .map(e -> MessageRecipient.builder()
                                .recipientType(MessageRecipient.RecipientType.PARENT)
                                .recipientName(e.getValue())
                                .recipientEmail(e.getKey())
                                .build())
                        .toList();
            }
        };
    }

    private MessageRecipient toStaffRecipient(Staff s) {
        return MessageRecipient.builder()
                .recipientType(MessageRecipient.RecipientType.STAFF)
                .recipientName(s.getFirstName() + " " + s.getLastName())
                .recipientEmail(s.getEmail())
                .build();
    }

    private User currentUser() {
        return userRepository.findByUserName(authService.getLoggedInUserName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private String displayName(User user) {
        String otherNames = user.getOtherNames();
        return otherNames != null && !otherNames.isBlank() ? user.getFirstName() + " " + otherNames : user.getFirstName();
    }
}
