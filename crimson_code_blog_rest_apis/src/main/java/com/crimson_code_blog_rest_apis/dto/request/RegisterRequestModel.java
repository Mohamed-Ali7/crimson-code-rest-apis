package com.crimson_code_blog_rest_apis.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestModel {

	@NotBlank(message = "Email cannot be Empty.")
	@Email(message = "Email must be a well-formed, like : name@crimson-code.com.")
	private String email;
	
	@NotBlank(message = "Password cannot be Empty.")
	@Size(min = 6, max = 30, message = "Password must be 6 to 30 characters long.")
	private String password;
	
	@NotBlank(message = "First name cannot be Empty.")
	@Size(min = 2, max = 30, message = "First name must be 2 to 30 characters long.")
	private String firstName;
	
	@Size(min = 2, max = 30, message = "Last name must be 2 to 30 characters long.")
	private String lastName;
	
	
	public RegisterRequestModel() {
		
	}


	public RegisterRequestModel(String email, String password, String firstName, String lastName) {
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}
