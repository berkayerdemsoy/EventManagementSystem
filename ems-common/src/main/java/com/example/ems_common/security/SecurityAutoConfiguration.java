package com.example.ems_common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@AutoConfiguration
@ConditionalOnClass(HttpSecurity.class)
public class SecurityAutoConfiguration {

    @Bean
    public JwtUtil jwtUtil(@Value("${jwt.secret}") String jwtSecret,
                           @Value("${jwt.expiration}") Long jwtExpiration) {
        return new JwtUtil(jwtSecret, jwtExpiration);
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil) {
        return new JwtAuthFilter(jwtUtil);
    }
}

