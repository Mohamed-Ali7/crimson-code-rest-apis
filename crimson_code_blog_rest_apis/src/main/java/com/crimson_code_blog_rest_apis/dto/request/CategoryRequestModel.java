package com.crimson_code_blog_rest_apis.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequestModel {

	@NotBlank(message = "Category name cannot be empty")
	String name;
	
	public CategoryRequestModel() {
		
	}

	public CategoryRequestModel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
