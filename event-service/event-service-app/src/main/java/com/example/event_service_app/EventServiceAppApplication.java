package com.example.event_service_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
	"com.example.event_service_app",
	"com.example.user_service_client"
})
public class EventServiceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventServiceAppApplication.class, args);
		System.out.println("EventServiceAppApplication started successfully.Testing CI/CD");
	}

}
