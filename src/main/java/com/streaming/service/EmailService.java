package com.streaming.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailService{

	@Autowired JavaMailSender mailSender;
	@Autowired SpringTemplateEngine templateEngine;
	@Autowired ObjectMapper objectMapper;
	
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	
	@Value("${spring.mail.username}")
	private String mailFrom;
		
	public boolean sendHtmlMail(String toAddress, String subject, Map<String, Object> emailBody, String templateName) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			Context context = new Context();
			context.setVariables(emailBody);
			String html = templateEngine.process(templateName, context);
			helper.setText(html, true);
			helper.setSubject(subject);
			helper.setFrom(mailFrom);
			helper.setTo(toAddress);
			mailSender.send(message);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	
}
