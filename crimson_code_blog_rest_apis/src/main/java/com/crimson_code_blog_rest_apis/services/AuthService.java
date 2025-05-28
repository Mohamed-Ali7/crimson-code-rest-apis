package com.crimson_code_blog_rest_apis.services;

import com.crimson_code_blog_rest_apis.dto.request.EmailVerificationRequest;
import com.crimson_code_blog_rest_apis.dto.request.LoginRequestModel;
import com.crimson_code_blog_rest_apis.dto.request.LogoutRequestModel;
import com.crimson_code_blog_rest_apis.dto.request.RegisterRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.LoginResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.RefreshTokenResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserResponseModel;

public interface AuthService {

	UserResponseModel register(RegisterRequestModel registerRequest);
	LoginResponseModel login(LoginRequestModel loginRequest);
	void emailVerification(String token);
	void emailVerificationRequest(EmailVerificationRequest verificationRequest);
	RefreshTokenResponseModel refreshAccessToken(String authorizationHeader);
	void logout(LogoutRequestModel logoutRequest, String authorizationHeader);
}
