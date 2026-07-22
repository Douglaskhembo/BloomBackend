package com.bloom.bloomschool.notiffication.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

    @Value("spring.mail.username")
    private String mailUsername;

    @Autowired
    private JavaMailSender javaMailSender;

    public void registrationEmail(String schoolName, String email, String firstName, String userName, String password) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(mailUsername);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(schoolName + " User Registration Confirmation");

        String content = "<html>" +
                "<body>" +
                "<h3>Dear " + firstName + "</h3>" +
                "<p>Welcome to " + schoolName +". Your account has been created successfully.</p>" +
                "<p>Your login credentials are as follows:</p>" +
                "<p><strong>Username:<strong> "+userName+"</p>" +
                "<p><strong>Password:<strong> "+password+"</p>" +
                "<br>" +
                "<p>Please make sure to change your password after first login.</p>" +
                "<p>Best Regards,</p>" +
                "<p>System Admin</p>" +
                "</body>" +
                "<html>";

        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendOtp(String email, String firstName, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(mailUsername);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("OTP verification");

        String content = "<html>" +
                "<body>" +
                "<h3>Dear " + firstName + "</h3>" +
                "<p>Your One Time Password (OTP) is: <strong>"+otp+"</strong></p>" +
                "<br>" +
                "<p>This OTP will expire in 3 minutes.</p>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<p>Best Regards,</p>" +
                "<p>System Admin</p>" +
                "</body>" +
                "<html>";

        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendPasswordResetLink(String email, String firstName, String resetLink) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(mailUsername);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Password Reset Request");

        String content = "<html>" +
                "<body>" +
                "<h3>Dear " + firstName + "</h3>" +
                "<p>We have received a request to reset your password.</p>" +
                "<p>Click on the link below to reset your password:</p>" +
                "<p><a href='"+resetLink+"'>Reset Password</a></p>" +
                "<br>" +
                "<p>This link will expire in 1 hour.</p>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<p>Best Regards,</p>" +
                "<p>System Admin</p>" +
                "</body>" +
                "<html>";

        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendPasswordChangedConfirmation(String email, String firstName) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(mailUsername);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Password Changed Confirmation");

        String content = "<html>" +
                "<body>" +
                "<h3>Dear " + firstName + "</h3>" +
                "<p>Your password has been changed successfully.</p>" +
                "<p>If you did not make this change, please contact us immediately.</p>" +
                "<p>Best Regards,</p>" +
                "<p>System Admin</p>" +
                "</body>" +
                "<html>";

        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendPasswordResetEmail(String email, String firstName, String schoolName, String password) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(mailUsername);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(schoolName + " - Password Reset");

        String content = "<html>" +
                "<body>" +
                "<h3>Dear " + firstName + "</h3>" +
                "<p>Your password has been reset successfully.</p>" +
                "<p>Your new password is: <strong>" + password + "</strong></p>" +
                "<p>Please make sure to change your password after first login.</p>" +
                "<p>Best Regards,</p>" +
                "<p>System Admin</p>" +
                "</body>" +
                "<html>";

        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendAccountDisabledEmail(String email, String firstName, String schoolName) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(mailUsername);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(schoolName + " - Account Disabled");

        String content = "<html>" +
                "<body>" +
                "<h3>Dear " + firstName + "</h3>" +
                "<p>Your account has been disabled by the administrator.</p>" +
                "<p>Please contact the administrator for further assistance.</p>" +
                "<p>Best Regards,</p>" +
                "<p>System Admin</p>" +
                "</body>" +
                "<html>";

        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendAccountEnabledEmail(String email, String firstName, String schoolName) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(mailUsername);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(schoolName + " - Account Enabled");

        String content = "<html>" +
                "<body>" +
                "<h3>Dear " + firstName + "</h3>" +
                "<p>Your account has been enabled by the administrator.</p>" +
                "<p>You can now login to your account.</p>" +
                "<p>Best Regards,</p>" +
                "<p>System Admin</p>" +
                "</body>" +
                "<html>";

        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendForgotUsernameEmail(String email, String firstName, String username) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(mailUsername);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Username Reminder");

        String content = "<html>" +
                "<body>" +
                "<h3>Dear " + firstName + "</h3>" +
                "<p>Your username is: <strong>" + username + "</strong></p>" +
                "<p>Best Regards,</p>" +
                "<p>System Admin</p>" +
                "</body>" +
                "<html>";

        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }
}
