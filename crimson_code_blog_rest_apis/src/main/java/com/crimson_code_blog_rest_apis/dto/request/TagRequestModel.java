package com.crimson_code_blog_rest_apis.dto.request;

import jakarta.validation.constraints.NotBlank;

public class TagRequestModel {

	@NotBlank(message = "Tag name cannot be empty")
	private String name;
	
	public TagRequestModel() {
		
	}

	public TagRequestModel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
