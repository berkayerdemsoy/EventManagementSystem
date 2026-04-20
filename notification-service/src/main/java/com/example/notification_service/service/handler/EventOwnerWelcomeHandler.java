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
public class EventOwnerWelcomeHandler implements NotificationHandler {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.from:onboarding@resend.dev}")
    private String fromEmail;

    @Override
    public NotificationEventType getHandledType() {
        return NotificationEventType.EVENT_OWNER_WELCOME;
    }

    @Override
    public void handle(NotificationEvent event) {
        String ownerName = event.getPayload().getOrDefault("ownerName", "Değerli Etkinlik Sahibi");
        String eventTitle = event.getPayload().getOrDefault("eventTitle", "");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(event.getRecipientEmail());
        message.setSubject("Eventer - Etkinliğiniz Başarıyla Oluşturuldu!");
        message.setText("""
                Merhaba %s,
                
                "%s" adlı etkinliğiniz başarıyla oluşturuldu. Tebrikler!
                
                Katılımcılar etkinliğinize kayıt olmaya başladığında bildirim alacaksınız.
                
                İyi etkinlikler dileriz,
                Eventer Ekibi
                """.formatted(ownerName, eventTitle));

        mailSender.send(message);
        log.info("[EventOwnerWelcome] Hoşgeldin maili gönderildi: {}", event.getRecipientEmail());
    }
}

