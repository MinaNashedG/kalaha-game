package com.kalaha.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class KalahaGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(KalahaGameApplication.class, args);
	}

}
