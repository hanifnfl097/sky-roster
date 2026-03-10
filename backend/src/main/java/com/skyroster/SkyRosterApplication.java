package com.skyroster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkyRosterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkyRosterApplication.class, args);
	}

}
