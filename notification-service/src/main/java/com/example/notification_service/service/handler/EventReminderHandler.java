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
public class EventReminderHandler implements NotificationHandler {

    private final EmailTemplateService emailTemplateService;

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

        Map<String, Object> variables = Map.of(
                "eventTitle", eventTitle,
                "eventDate",  eventDate,
                "eventCity",  eventCity
        );

        emailTemplateService.sendHtmlEmail(
                fromEmail,
                event.getRecipientEmail(),
                "Eventer - Yarınki Etkinliğinizi Unutmayın!",
                "mail/event-reminder",
                variables
        );

        log.info("[EventReminder] Hatırlatma maili gönderildi: {}", event.getRecipientEmail());
    }
}
