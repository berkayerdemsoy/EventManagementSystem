package com.example.user_service_app.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${gateway.url:http://localhost:8090}")
    private String gatewayUrl;

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .description("""
                                User management, authentication, and email verification service.
                                
                                **Authentication:** Use `POST /users/login` to get a JWT token,
                                then click **Authorize** and enter: `Bearer <token>`
                                """)
                        .version("v1.0")
                        .contact(new Contact()
                                .name("EMS Backend")
                                .url("https://github.com/berkayerdemsoy/EventManagementSystem")))
                .servers(List.of(
                        new Server().url(gatewayUrl).description("API Gateway (default)"),
                        new Server().url("http://localhost:8080").description("Direct (local dev)")
                ))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your JWT token from `/users/login` response.")));
    }
}

