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
public class EmailVerificationHandler implements NotificationHandler {

    private final EmailTemplateService emailTemplateService;

    @Value("${notification.mail.from:onboarding@resend.dev}")
    private String fromEmail;

    @Override
    public NotificationEventType getHandledType() {
        return NotificationEventType.EMAIL_VERIFICATION;
    }

    @Override
    public void handle(NotificationEvent event) {
        String verificationLink = event.getPayload().getOrDefault("verificationLink", "");

        Map<String, Object> variables = Map.of(
                "verificationLink", verificationLink
        );

        emailTemplateService.sendHtmlEmail(
                fromEmail,
                event.getRecipientEmail(),
                "Eventer - Lütfen Hesabınızı Doğrulayın",
                "mail/email-verification",
                variables
        );

        log.info("[EmailVerification] Doğrulama maili gönderildi: {}", event.getRecipientEmail());
    }
}
