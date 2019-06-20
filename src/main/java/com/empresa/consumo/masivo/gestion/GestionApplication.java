package com.empresa.consumo.masivo.gestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GestionApplication {
	public static void main(String[] args) {
		SpringApplication.run(GestionApplication.class, args);
	}
}

