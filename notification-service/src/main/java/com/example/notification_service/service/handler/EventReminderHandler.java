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
public class EventReminderHandler implements NotificationHandler {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.from:onboarding@resend.dev}")
    private String fromEmail;

    @Override
    public NotificationEventType getHandledType() {
        return NotificationEventType.EVENT_REMINDER;
    }

    @Override
    public void handle(NotificationEvent event) {
        String eventTitle = event.getPayload().getOrDefault("eventTitle", "");
        String eventDate  = event.getPayload().getOrDefault("eventDate", "");
        String eventCity  = event.getPayload().getOrDefault("eventCity", "");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(event.getRecipientEmail());
        message.setSubject("Eventer - Yarınki Etkinliğinizi Unutmayın!");
        message.setText("""
                Merhaba,
                
                Yarın gerçekleşecek "%s" etkinliğini hatırlatmak istedik.
                
                📅 Tarih: %s
                📍 Şehir: %s
                
                İyi eğlenceler dileriz!
                Eventer Ekibi
                """.formatted(eventTitle, eventDate, eventCity));

        mailSender.send(message);
        log.info("[EventReminder] Hatırlatma maili gönderildi: {}", event.getRecipientEmail());
    }
}

