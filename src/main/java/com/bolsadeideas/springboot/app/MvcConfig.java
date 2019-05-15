package com.bolsadeideas.springboot.app;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		WebMvcConfigurer.super.addResourceHandlers(registry);
		
		// .toUri() incluye el esquema file:/
		String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();

		log.info("#############MIERDAAAAAAAAAAAAAAAAAA ###########: " + resourcePath);

		registry.addResourceHandler("/uploads/**").addResourceLocations(resourcePath);
	}

}
