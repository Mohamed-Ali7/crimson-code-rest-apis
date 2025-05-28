package com.crimson_code_blog_rest_apis.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public class PostResponseModel {
	private long id;
	private String title;
	private String content;
	private String imageUrl;
	private CategoryResponseModel category;
	private UserSummaryResponseModel user;
	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;
	private List<TagResponseModel> tags;
	
	public PostResponseModel() {
		
	}

	public PostResponseModel(long id, String title, String content, String imageUrl, CategoryResponseModel category,
			UserSummaryResponseModel user, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.category = category;
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public CategoryResponseModel getCategory() {
		return category;
	}

	public void setCategory(CategoryResponseModel category) {
		this.category = category;
	}

	public UserSummaryResponseModel getUser() {
		return user;
	}

	public void setUser(UserSummaryResponseModel user) {
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

	public List<TagResponseModel> getTags() {
		return tags;
	}

	public void setTags(List<TagResponseModel> tags) {
		this.tags = tags;
	}
	
}
