package com.example.user_service_client.client;

import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String SERVICE_URL = "http://user-service-app";

    @Autowired(required = false)
    private List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

    @Bean
    public UserServiceClient userServiceClient(
            @Autowired(required = false) @org.springframework.beans.factory.annotation.Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder lbBuilder) {

        RestClient.Builder builder = (lbBuilder != null) ? lbBuilder : RestClient.builder();

        RestClient restClient = builder
                .baseUrl(SERVICE_URL)
                .requestInterceptors(list -> list.addAll(
                        interceptors.stream()
                                .filter(i -> !i.getClass().getName().contains("LoadBalancer"))
                                .toList()
                ))
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(UserServiceClient.class);
    }
}
