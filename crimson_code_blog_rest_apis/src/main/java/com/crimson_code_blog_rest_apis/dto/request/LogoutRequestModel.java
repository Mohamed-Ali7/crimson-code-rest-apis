package com.crimson_code_blog_rest_apis.dto.request;

public class LogoutRequestModel {
	
	private String refreshToken;

	public LogoutRequestModel() {
		
	}

	public LogoutRequestModel(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}