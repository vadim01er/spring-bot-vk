package com.vadim01er.springbotvk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.vadim01er.springbotvk")
@EntityScan("com.vadim01er.springbotvk")
@EnableScheduling
public class SpringBotVkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBotVkApplication.class, args);
	}

}
