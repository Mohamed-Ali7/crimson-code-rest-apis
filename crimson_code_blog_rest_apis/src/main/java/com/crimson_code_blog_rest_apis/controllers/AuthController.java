package com.crimson_code_blog_rest_apis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crimson_code_blog_rest_apis.dto.request.EmailVerificationRequest;
import com.crimson_code_blog_rest_apis.dto.request.LoginRequestModel;
import com.crimson_code_blog_rest_apis.dto.request.LogoutRequestModel;
import com.crimson_code_blog_rest_apis.dto.request.RegisterRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.LoginResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.OperationStatusResponse;
import com.crimson_code_blog_rest_apis.dto.response.RefreshTokenResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserResponseModel;
import com.crimson_code_blog_rest_apis.services.AuthService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/api/auth")
public class AuthController {

	private AuthService authService;
	
	@Autowired
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/register")
	ResponseEntity<UserResponseModel> register(@Valid @RequestBody RegisterRequestModel registerRequest) {
		
		return new ResponseEntity<>(authService.register(registerRequest), HttpStatus.CREATED);
	}
	
	@PostMapping("/login")
	ResponseEntity<LoginResponseModel> login(@Valid @RequestBody LoginRequestModel loginRequest) {
		
		return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
	}
	
	@GetMapping("/email-verification")
	ResponseEntity<OperationStatusResponse> emailVerification(@RequestParam("token") String verificationToken) {
		
		OperationStatusResponse operationResponse = new OperationStatusResponse();
		
		operationResponse.setOperationName(OperationName.VERIFIY_USER_EMAIL.name());
		operationResponse.setOperationStatus(OperationStatus.SUCCESS.name());
		operationResponse.setMessage("Email verified successfully");
		
		authService.emailVerification(verificationToken);
		
		return new ResponseEntity<>(operationResponse, HttpStatus.OK);
	}
	
	@PostMapping("/email-verification-request")
	ResponseEntity<OperationStatusResponse> emailVerificationRequest(
			@Valid @RequestBody EmailVerificationRequest verificationRequest) {
		
		OperationStatusResponse operationResponse = new OperationStatusResponse();
		
		operationResponse.setOperationName(OperationName.EMAIL_VERIFICATION_TOKEN_REQUEST.name());
		operationResponse.setOperationStatus(OperationStatus.SUCCESS.name());
		operationResponse.setMessage("Verification email sent successfully");

		authService.emailVerificationRequest(verificationRequest);
		
		return new ResponseEntity<>(operationResponse, HttpStatus.OK);
	}
	
	@GetMapping("/refresh")
	public ResponseEntity<RefreshTokenResponseModel> refreshAccessToken (HttpServletRequest request) {

		RefreshTokenResponseModel refreshResponse = 
				authService.refreshAccessToken(request.getHeader(HttpHeaders.AUTHORIZATION));
		
		return new ResponseEntity<>(refreshResponse, HttpStatus.OK);
	}
	
	@PostMapping("/logout")
	ResponseEntity<OperationStatusResponse> logout(
			@RequestBody LogoutRequestModel logoutRequest, HttpServletRequest request) {
		
		OperationStatusResponse operationResponse = new OperationStatusResponse();
		
		operationResponse.setOperationName(OperationName.LOGOUT.name());
		operationResponse.setOperationStatus(OperationStatus.SUCCESS.name());
		operationResponse.setMessage("Logged out successfully");

		authService.logout(logoutRequest, request.getHeader(HttpHeaders.AUTHORIZATION));
		
		return new ResponseEntity<>(operationResponse, HttpStatus.OK);
	}
	
}
