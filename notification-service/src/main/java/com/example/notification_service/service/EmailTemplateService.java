package com.example.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Thymeleaf şablon motoru ile HTML e-posta gönderimi.
 * Tüm handler'lar doğrudan JavaMailSender yerine bu servisi kullanır.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    /**
     * Verilen Thymeleaf şablonunu işleyerek HTML e-posta gönderir.
     *
     * @param from         Gönderici adresi
     * @param to           Alıcı adresi
     * @param subject      E-posta konusu
     * @param templateName resources/templates/ altındaki şablon adı (uzantısız)
     * @param variables    Şablona aktarılacak değişkenler
     */
    public void sendHtmlEmail(String from,
                              String to,
                              String subject,
                              String templateName,
                              Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("[EmailTemplateService] HTML mail gönderildi → to={}, template={}", to, templateName);

        } catch (MessagingException e) {
            log.error("[EmailTemplateService] Mail gönderilemedi: {}", e.getMessage(), e);
            throw new RuntimeException("Mail gönderilemedi", e);
        }
    }
}

