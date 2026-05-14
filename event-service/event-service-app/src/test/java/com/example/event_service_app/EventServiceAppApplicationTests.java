package com.example.event_service_app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.loadbalancer.LoadBalancerEurekaAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {
        KafkaAutoConfiguration.class,
        EurekaClientAutoConfiguration.class,
        LoadBalancerEurekaAutoConfiguration.class
})
class EventServiceAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
