package com.crimson_code_blog_rest_apis.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CommentRequestModel {

	@NotBlank(message = "Comment cannot be empty")
	private String content;
	
	public CommentRequestModel() {
		
	}

	public CommentRequestModel(@NotBlank(message = "Comment cannot be empty") String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
