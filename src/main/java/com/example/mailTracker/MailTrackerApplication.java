package com.example.mailTracker;

import com.example.mailTracker.service.EmailParser;
import com.example.mailTracker.service.EmailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MailTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailTrackerApplication.class, args);
	}


	@Bean
	public EmailService emailService() {
		return new EmailService();
	}

	@Bean
	public EmailParser emailParser() {
		return new EmailParser();
	}
}
