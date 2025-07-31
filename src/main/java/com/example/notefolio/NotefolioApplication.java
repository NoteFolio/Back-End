package com.example.notefolio;

import com.example.notefolio.security.jwt.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class NotefolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotefolioApplication.class, args);
	}

}
