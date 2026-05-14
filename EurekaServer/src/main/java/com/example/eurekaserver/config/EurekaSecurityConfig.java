package com.example.eurekaserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class EurekaSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Eureka istemcileri POST ile kayıt olur; sadece /eureka/** için CSRF muafiyeti yeterli
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/eureka/**"))
                .authorizeHttpRequests(auth -> auth
                        // Docker healthcheck ve monitoring için actuator/health herkese açık
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
