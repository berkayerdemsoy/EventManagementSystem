package com.example.user_service_app.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String verificationLink);
}
