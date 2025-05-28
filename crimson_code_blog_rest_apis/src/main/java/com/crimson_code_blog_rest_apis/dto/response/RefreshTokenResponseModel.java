
package com.crimson_code_blog_rest_apis.dto.response;

public class RefreshTokenResponseModel {
	
	private String accessToken;

	public RefreshTokenResponseModel() {
		
	}
	
	public RefreshTokenResponseModel(String token) {
		this.accessToken = token;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String token) {
		this.accessToken = token;
	}
	
}
