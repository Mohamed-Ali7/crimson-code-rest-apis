package com.crimson_code_blog_rest_apis.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.crimson_code_blog_rest_apis.exceptions.CrimsonCodeGlobalException;
import com.crimson_code_blog_rest_apis.services.EmailService;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
	@Value("${frontendVerifyEmailUrl}")
	private String frontendVerifyEmailUrl;
	
	@Value("${frontendPasswordResetUrl}")
	private String frontendPasswordResetUrl;
	private final String FROM = "crimson_code.email@gmail.com";

	private final String EMAIL_VERIFICATION_SUBJECT = "Verify Your Email Address to Finish Your Registration Proccess";


	private String emailVerificationHtmlBody;
	
	private final String PASSWORD_RESET_SUBJECT = "Password Reset Request";
	
	private String passwordResetHtmlBody;

	private JavaMailSender javaMailSender;

	@Autowired
	public EmailServiceImpl(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@PostConstruct
	public void init() {
		emailVerificationHtmlBody = "<h1>Please verify your email address</h1> "
				+ "<p>Click on the following link to verify your email address</p>"
				+ "<a href='" + frontendVerifyEmailUrl + "?token=${tokenValue}'>"
				+ "Your link to complete your registration</a>";
		
		
		passwordResetHtmlBody = "<h1>Due to Your Request to Reset Your Password</h1> "
				+ "<p>Click on the following link to proceed your password reset process</p>"
				+ "<a href='" + frontendPasswordResetUrl + "?token=${tokenValue}'>"
				+ "Your link to reset your password</a>";
	    
	}
	
	@Override
	public void sendVerificationEmail(String email, String token) {
		String htmlBodyWithToken = emailVerificationHtmlBody.replace("${tokenValue}", token);
		sendEmail(email, EMAIL_VERIFICATION_SUBJECT, htmlBodyWithToken);
	}

	@Override
	public void sendPasswordResetEmail(String email, String token) {
		String htmlBodyWithToken = passwordResetHtmlBody.replace("${tokenValue}", token);
		sendEmail(email, PASSWORD_RESET_SUBJECT, htmlBodyWithToken);
		
	}
	
	private void sendEmail(String sendTo, String subject, String htmlBody) {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

		try {
			mimeMessageHelper.setTo(sendTo);
			mimeMessageHelper.setFrom(FROM);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(htmlBody, true);
			

		} catch (MessagingException ex) {
			throw new CrimsonCodeGlobalException(ex.getMessage());
		}

		javaMailSender.send(mimeMessage);
	}
}
