package com.crimson_code_blog_rest_apis.exceptions;

import com.crimson_code_blog_rest_apis.utils.JwtTokenType;

public class JwtTokenException extends RuntimeException {

	private JwtTokenType tokenType;

	public JwtTokenException(JwtTokenType tokenType , String message) {
		super(message);
		this.tokenType = tokenType;
	}

	public JwtTokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(JwtTokenType tokenType) {
		this.tokenType = tokenType;
	}
}
