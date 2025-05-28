package com.crimson_code_blog_rest_apis.exceptions;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CrimsonCodeGlobalException.class)
	public ResponseEntity<ErrorResponse> handleCrimsonCodeGlobalException(
			CrimsonCodeGlobalException ex, HttpServletRequest request) {
		
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getRequestURI());
		
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(JwtTokenException.class)
	public ResponseEntity<ErrorResponse> handleAccessRefreshTokenException(
			JwtTokenException ex, HttpServletRequest request) {

		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

		/*
		 * If the token is access or refresh token the returned status code will be 401 (Unauthorized)
		 * otherwise the returned status code will be 400 (Bad Request)
		 */

		if (ex.getTokenType() != null && ex.getTokenType().isAuthToken()) {
		    httpStatus = HttpStatus.UNAUTHORIZED;
		}

		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),
				httpStatus.value(), ex.getMessage(), request.getRequestURI());
		
		return new ResponseEntity<>(errorResponse, httpStatus);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
			ResourceNotFoundException ex, HttpServletRequest request) {

		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),
				HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ErrorResponse> handleDisabledException(
			DisabledException ex, HttpServletRequest request) {

		String message = "Account disabled due to unverified email address, please verifiy your email first";
		
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),
				HttpStatus.FORBIDDEN.value(), message, request.getRequestURI());
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(
	        MethodArgumentNotValidException ex, HttpServletRequest request) {

	    String errorMsg = ex.getBindingResult().getFieldErrors().stream()
	            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
	            .collect(Collectors.joining(", "));

	    ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),
	            HttpStatus.BAD_REQUEST.value(), errorMsg, request.getRequestURI());

	    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}
