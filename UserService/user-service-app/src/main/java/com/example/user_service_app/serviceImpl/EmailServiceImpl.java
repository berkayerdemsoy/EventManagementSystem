package com.example.user_service_app.serviceImpl;

import com.example.user_service_app.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private String fromEmail = "onboarding@resend.dev";


    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Eventer - Lütfen Hesabınızı Doğrulayın");


        String mailText = "Merhaba,\n\n"
                + "Eventer'a hoş geldiniz! Hesabınızı aktifleştirmek için lütfen aşağıdaki bağlantıya tıklayın:\n\n"
                + verificationLink + "\n\n"
                + "Bu bağlantı 24 saat boyunca geçerlidir.\n\n"
                + "İyi günler dileriz.";

        message.setText(mailText);


        mailSender.send(message);
    }
}
