package com.crimson_code_blog_rest_apis.utils;

public enum JwtTokenType {
	ACCESS_TOKEN("Access"),
	REFRESH_TOKEN("Refresh"),
	EMAIL_VERIFICATION_TOKEN("Email verification"),
	PASSWORD_RESET_TOKEN("Password reset");
	
	String value;
	
	private JwtTokenType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isAuthToken() {
		return this == ACCESS_TOKEN || this == REFRESH_TOKEN;
	}
}
