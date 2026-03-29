package com.example.user_service_app.configs.emailConfigs;

import com.example.user_service_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByTokenHash(String tokenHash);
    Optional<VerificationToken> findByUser(User user);
}
