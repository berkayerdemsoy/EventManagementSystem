package com.example.notification_service.service.handler;

import com.example.ems_common.dto.NotificationEvent;
import com.example.ems_common.dto.NotificationEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailVerificationHandler implements NotificationHandler {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.from:onboarding@resend.dev}")
    private String fromEmail;

    @Override
    public NotificationEventType getHandledType() {
        return NotificationEventType.EMAIL_VERIFICATION;
    }

    @Override
    public void handle(NotificationEvent event) {
        String verificationLink = event.getPayload().getOrDefault("verificationLink", "");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(event.getRecipientEmail());
        message.setSubject("Eventer - Lütfen Hesabınızı Doğrulayın");
        message.setText("""
                Merhaba,
                
                Eventer'a hoş geldiniz! Hesabınızı aktifleştirmek için lütfen aşağıdaki bağlantıya tıklayın:
                
                %s
                
                Bu bağlantı 24 saat boyunca geçerlidir.
                
                İyi günler dileriz.
                """.formatted(verificationLink));

        mailSender.send(message);
        log.info("[EmailVerification] Doğrulama maili gönderildi: {}", event.getRecipientEmail());
    }
}

