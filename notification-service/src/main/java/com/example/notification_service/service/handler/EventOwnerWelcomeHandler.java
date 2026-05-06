package com.example.notification_service.service.handler;

import com.example.ems_common.dto.NotificationEvent;
import com.example.ems_common.dto.NotificationEventType;
import com.example.notification_service.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventOwnerWelcomeHandler implements NotificationHandler {

    private final EmailTemplateService emailTemplateService;

    @Value("${notification.mail.from:onboarding@resend.dev}")
    private String fromEmail;

    @Override
    public NotificationEventType getHandledType() {
        return NotificationEventType.EVENT_OWNER_WELCOME;
    }

    @Override
    public void handle(NotificationEvent event) {
        String ownerName  = event.getPayload().getOrDefault("ownerName", "Değerli Etkinlik Sahibi");
        String eventTitle = event.getPayload().getOrDefault("eventTitle", "");

        Map<String, Object> variables = Map.of(
                "ownerName",  ownerName,
                "eventTitle", eventTitle
        );

        emailTemplateService.sendHtmlEmail(
                fromEmail,
                event.getRecipientEmail(),
                "Eventer - Etkinliğiniz Başarıyla Oluşturuldu!",
                "mail/event-owner-welcome",
                variables
        );

        log.info("[EventOwnerWelcome] Hoşgeldin maili gönderildi: {}", event.getRecipientEmail());
    }
}
