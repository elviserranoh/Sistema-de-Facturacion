package com.bolsadeideas.springboot.app;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

//	private Logger log = LoggerFactory.getLogger(getClass());

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		// TODO Auto-generated method stub
//		WebMvcConfigurer.super.addResourceHandlers(registry);
//		
//		// .toUri() incluye el esquema file:/
//		String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
//
//		log.info("#############MIERDAAAAAAAAAAAAAAAAAA ###########: " + resourcePath);
//
//		registry.addResourceHandler("/uploads/**").addResourceLocations(resourcePath);
//	}

}
