package com.streaming.config;

import java.nio.charset.StandardCharsets;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.streaming.utils.InMeetingMembers;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ApplicationConfiguration {
	
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKeyId;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretAccessKey;

	@Value("${cloud.aws.region.static}")
	private String region;
	
	@Bean
	public AmazonS3 getAmazonS3Cient() {
		final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region))
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials)).build();
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
		
	@Bean
	public InMeetingMembers inMeetingMembers() {
		return new InMeetingMembers();
	}
	
	@Bean
	public SpringTemplateEngine springTemplateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(htmlTemplateResolver());
		return templateEngine;
	}

	@Bean
	public SpringResourceTemplateResolver htmlTemplateResolver() {
		SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
		emailTemplateResolver.setPrefix("classpath:/templates/");
		emailTemplateResolver.setSuffix(".html");
		emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
		emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		return emailTemplateResolver;
	}
		
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
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
	    };
	}
}
