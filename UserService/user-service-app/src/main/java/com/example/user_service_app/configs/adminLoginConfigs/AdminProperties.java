package com.example.user_service_app.configs.adminLoginConfigs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {
    private List<String> usernames = new ArrayList<>();
    private List<String> passwords = new ArrayList<>();
}
