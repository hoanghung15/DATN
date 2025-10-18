package com.example.datnbe.base.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordMail(String email, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("Your new password is: " + newPassword);
        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String link) {
        String subject = "Xác nhận tài khoản của bạn";
        String content = "<p>Chào bạn,</p>"
                + "<p>Vui lòng nhấn nút bên dưới để xác thực tài khoản:</p>"
                + "<a href=\"" + link + "\" "
                + "style=\"display:inline-block;"
                + "padding:10px 20px;"
                + "background-color:#4CAF50;"
                + "color:#ffffff;"
                + "text-decoration:none;"
                + "border-radius:5px;"
                + "font-weight:bold;\">"
                + "XÁC NHẬN TÀI KHOẢN</a>"
                + "<p>Link sẽ hết hạn sau 30 phút.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi email xác thực", e);
        }
    }

    public void sendOTP(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }

    @KafkaListener(topics = "confirm-account-topic", groupId = "confirm-account-group")
    public void sendOTPByKafka(String messageFromKafka) {
        try {
            String[] parts = messageFromKafka.split("-", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid message format");
            }

            String email = parts[0].trim();
            String otp = parts[1].trim();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("OTP");
            message.setText("Your OTP is: " + otp);
            mailSender.send(message);
            log.info("Sent OTP mail to {}", email + " " +  otp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
