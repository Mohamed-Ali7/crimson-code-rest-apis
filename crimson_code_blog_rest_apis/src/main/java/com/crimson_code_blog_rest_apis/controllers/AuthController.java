package com.crimson_code_blog_rest_apis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crimson_code_blog_rest_apis.dto.request.EmailVerificationRequest;
import com.crimson_code_blog_rest_apis.dto.request.LoginRequestModel;
import com.crimson_code_blog_rest_apis.dto.request.LogoutRequestModel;
import com.crimson_code_blog_rest_apis.dto.request.RegisterRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.LoginResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.OperationStatusResponse;
import com.crimson_code_blog_rest_apis.dto.response.RefreshTokenResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserResponseModel;
import com.crimson_code_blog_rest_apis.exceptions.ErrorResponse;
import com.crimson_code_blog_rest_apis.services.AuthService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/api/auth")
@Tag(name = "Authentication APIs", description = "Endpoints for user login, registration, and token management")
public class AuthController {

	private AuthService authService;
	
	@Autowired
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/register")
	@Operation(summary = "Register a new user", description = "Registers a new user and sends an email verification token",
	responses = {
			@ApiResponse(responseCode = "201", description = "User registered successfully"),
			@ApiResponse(responseCode = "400", description = "Email already in use",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)}
	)
	
	ResponseEntity<UserResponseModel> register(@Valid @RequestBody RegisterRequestModel registerRequest) {
		
		return new ResponseEntity<>(authService.register(registerRequest), HttpStatus.CREATED);
	}
	
	@PostMapping("/login")
	@Operation(summary = "Login user", description = "Authenticates the user and returns an access and refresh token",
	responses = {
			@ApiResponse(responseCode = "200", description = "Login successful"),
			@ApiResponse(responseCode = "401", description = "Invalid credentials",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)}
	)
	ResponseEntity<LoginResponseModel> login(@Valid @RequestBody LoginRequestModel loginRequest) {
		
		return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
	}
	
	@GetMapping("/email-verification")
	@Operation(summary = "Verify email",
	        description = "Verifies a user's email using a token sent via email",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Email verified successfully"),
	        		@ApiResponse(responseCode = "400", description = "Invalid or expired token",
	        		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	ResponseEntity<OperationStatusResponse> emailVerification(@RequestParam("token") String verificationToken) {
		
		OperationStatusResponse operationResponse = new OperationStatusResponse();
		
		operationResponse.setOperationName(OperationName.VERIFIY_USER_EMAIL.name());
		operationResponse.setOperationStatus(OperationStatus.SUCCESS.name());
		operationResponse.setMessage("Email verified successfully");
		
		authService.emailVerification(verificationToken);
		
		return new ResponseEntity<>(operationResponse, HttpStatus.OK);
	}
	
	@PostMapping("/email-verification-request")
	@Operation(summary = "Request new email verification token",
	        description = "Sends a new email verification token to the registered user",
	        responses = {
	        		 @ApiResponse(responseCode = "200", description = "Verification email sent"),
	        		 @ApiResponse(responseCode = "404", description = "User not found",
	        		 	content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		 ),
	        		 @ApiResponse(responseCode = "400", description = "User's email has been already verified",
        			 	content = @Content( schema = @Schema(implementation = ErrorResponse.class))
	        		 )}
	)
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
	@Operation(summary = "Refresh access token",
	        description = "Generates a new access token using a valid refresh token",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
	        		@ApiResponse(responseCode = "401", description = "Refresh token expired or invalid",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<RefreshTokenResponseModel> refreshAccessToken (HttpServletRequest request) {

		RefreshTokenResponseModel refreshResponse = 
				authService.refreshAccessToken(request.getHeader(HttpHeaders.AUTHORIZATION));
		
		return new ResponseEntity<>(refreshResponse, HttpStatus.OK);
	}
	
	@PostMapping("/logout")
	@Operation(
			summary = "Logout user",
	        description = "Invalidates (Blacklisting) the refresh and access tokens and logs the user out",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "User logged out successfully"),
	        		@ApiResponse(responseCode = "401", description = "User is not logged (access token expired or invalid)",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
			)
	@SecurityRequirement(name = "bearerAuth")
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
