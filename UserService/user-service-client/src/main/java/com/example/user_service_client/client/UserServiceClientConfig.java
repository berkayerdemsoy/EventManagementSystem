package com.example.user_service_client.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class UserServiceClientConfig {

    @Value("${user-service.url:http://localhost:8080}")
    private String userServiceBaseUrl;

    @Autowired(required = false)
    private List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

    @Bean
    public UserServiceClient userServiceClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(userServiceBaseUrl)
                .requestInterceptors(list -> list.addAll(interceptors))
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(UserServiceClient.class);
    }
}
