package com.crimson_code_blog_rest_apis.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class PasswordResetRequestModel {
	
	@NotBlank(message = "Email cannot be Empty.")
	@Email(message = "Email must be a well-formed, like : name@crimson-code.com.")
	private String email;

	public PasswordResetRequestModel() {

	}

	public PasswordResetRequestModel(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}