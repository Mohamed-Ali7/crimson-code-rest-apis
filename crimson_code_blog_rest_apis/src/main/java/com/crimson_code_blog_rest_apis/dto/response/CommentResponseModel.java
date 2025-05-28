package com.crimson_code_blog_rest_apis.dto.response;

import java.time.OffsetDateTime;

public class CommentResponseModel {

	private long id;
	private String content;
	private UserResponseModel user;
	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;
	
	public CommentResponseModel() {
		
	}

	public CommentResponseModel(long id, String content, UserResponseModel user, OffsetDateTime createdAt,
			OffsetDateTime updatedAt) {
		this.id = id;
		this.content = content;
		this.user = user;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserResponseModel getUser() {
		return user;
	}

	public void setUser(UserResponseModel user) {
		this.user = user;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(OffsetDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
