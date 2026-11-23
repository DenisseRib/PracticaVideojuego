package com.dawes.videojuegos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI apiInfo() {
		return new OpenAPI()
				.info(new Info()
						.title("API Gestión de Videojuegos")
						.version("1.0.0")
						.description("API REST para la gestión de una biblioteca de videojuegos: "
								+ "socios, catálogo de videojuegos y control de préstamos "
								+ "(altas, consultas, renovaciones y devoluciones con multas).")
						.contact(new Contact()
								.name("DAWES")
								.email("dawes@ejemplo.com")))
				.addServersItem(new Server()
						.url("http://localhost:8080")
						.description("Servidor local"));
	}
}
