package com.example.user_service_app.configs.frontendConfigs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "frontend")
public class FrontendProperties {
    private String url = "http://localhost:4200";
}

