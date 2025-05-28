package com.crimson_code_blog_rest_apis.services;

public interface EmailService {

	void sendVerificationEmail(String email, String token);
	void sendPasswordResetEmail(String email, String token);
}
