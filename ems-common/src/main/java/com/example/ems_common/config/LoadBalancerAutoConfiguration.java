package com.example.ems_common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

/**
 * Auto-configuration that provides a load-balanced {@link RestClient.Builder}.
 * Only activated when Spring Cloud LoadBalancer is on the classpath.
 * All service-to-service client configs can inject this builder by name.
 */
@AutoConfiguration
//bu sekilde compile asamasinda bellege yukleyerek kontrol etmesini engellemis oluyor. direkt olarak string kontrolu yapip cikiyor.(diger yontem Loadbalancer.class)
@ConditionalOnClass(name = "org.springframework.cloud.client.loadbalancer.LoadBalanced")
public class LoadBalancerAutoConfiguration {

    @Bean("loadBalancedRestClientBuilder")
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }
}

