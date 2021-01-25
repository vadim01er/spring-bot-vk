package com.vadim01er.springbotvk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.vadim01er.springbotvk.entities")
public class SpringBotVkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBotVkApplication.class, args);
	}

}
