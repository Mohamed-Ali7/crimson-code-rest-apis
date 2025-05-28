package com.crimson_code_blog_rest_apis.dto.response;

public class LoginResponseModel {

	private String accessToken;
	private String refreshToken;
	
	public LoginResponseModel() {
		
	}

	public LoginResponseModel(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}