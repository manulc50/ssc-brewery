package com.mlorenzo.brewery.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configuración de CORS a nivel global, es decir, para todos los controladores de la aplicación.

// Se comenta porque ahora configuramos CORS únicamente en el controlador "BeerRestController".
//@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("GET", "POST", "PUT");
	}

}
