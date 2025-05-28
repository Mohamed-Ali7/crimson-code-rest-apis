package com.crimson_code_blog_rest_apis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequestModel {

	@NotBlank(message = "First name cannot be Empty.")
	@Size(min = 2, max = 30, message = "First name must be 2 to 30 characters long.")
	private String firstName;
	
	@NotBlank(message = "Last name cannot be Empty.")
	@Size(min = 2, max = 30, message = "Last name must be 2 to 30 characters long.")
	private String lastName;
	
	public UpdateUserRequestModel() {
		
	}

	public UpdateUserRequestModel(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
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