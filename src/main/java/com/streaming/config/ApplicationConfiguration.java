package com.streaming.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableWebMvc
public class ApplicationConfiguration {
	
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	
	@SuppressWarnings("deprecation")
	public static final ApiInfo DEFAULT_API_INFO = new ApiInfo("The Streaming Service APIs",
			null, null, null, null, null, null);

	private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(
			Arrays.asList("application/json"));

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(DEFAULT_API_INFO)
				.produces(DEFAULT_PRODUCES_AND_CONSUMES)
				.consumes(DEFAULT_PRODUCES_AND_CONSUMES)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.streaming"))
				.build();
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
	    return new WebMvcConfigurer () {
	        @Override
	        public void addCorsMappings(CorsRegistry registry) {
	            registry.addMapping("/**")
	            	.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
	            	.allowCredentials(true)
	            	.allowedOriginPatterns("*");
	        }
	        
	        @Override
	        public void addResourceHandlers(ResourceHandlerRegistry registry) {
	            registry.addResourceHandler("/qr/**").addResourceLocations("classpath:/static/qr/");
	        }
	    };
	}
}
