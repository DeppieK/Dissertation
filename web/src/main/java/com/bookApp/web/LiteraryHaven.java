package com.bookApp.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

//@Profile("favicon")
@SpringBootApplication
public class LiteraryHaven {

	public static void main(String[] args) {
		SpringApplication.run(LiteraryHaven.class, args);
	}

}
