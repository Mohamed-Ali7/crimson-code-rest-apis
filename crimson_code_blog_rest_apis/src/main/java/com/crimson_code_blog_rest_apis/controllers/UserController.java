package com.crimson_code_blog_rest_apis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crimson_code_blog_rest_apis.dto.request.ChangePasswordRequestModel;
import com.crimson_code_blog_rest_apis.dto.request.PasswordResetConfirmationRequest;
import com.crimson_code_blog_rest_apis.dto.request.PasswordResetRequestModel;
import com.crimson_code_blog_rest_apis.dto.request.UpdateUserRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.OperationStatusResponse;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserResponseModel;
import com.crimson_code_blog_rest_apis.security.UserPrincipal;
import com.crimson_code_blog_rest_apis.services.UserService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PutMapping("/me/profile-picture")
	public ResponseEntity<OperationStatusResponse> updateProfilePicture(
			@RequestParam("profilePicture") MultipartFile profilePicture) {
		
		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		userService.updateProfilePicture(profilePicture);
		
		operationStatus.setOperationName(OperationName.UPDATE_PROFILE_PICTURE.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("Your profile picture has been successfully updated.");
		
		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserResponseModel> getCurrentUser( @AuthenticationPrincipal UserPrincipal userPrincipal) {
		return new ResponseEntity<>(userService.getCurrentUser(userPrincipal), HttpStatus.OK);
	}

	@GetMapping("/{publicId}")
	public ResponseEntity<UserResponseModel> getUser(@PathVariable String publicId) {
		return new ResponseEntity<>(userService.getUser(publicId), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<PageResponseModel<UserResponseModel>> getAllUsers(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir
			){
		
		return new ResponseEntity<>(userService.getAllUser(page, pageSize, sortBy, sortDir), HttpStatus.OK);
	}
	
	@PreAuthorize("principal.publicId == #publicId")
	@PutMapping("/{publicId}")
	public ResponseEntity<UserResponseModel> updateUser(@PathVariable String publicId,
			@Valid @RequestBody UpdateUserRequestModel updateRequest) {
		
		return new ResponseEntity<>(userService.updateUser(publicId, updateRequest), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN') or principal.publicId == #publicId")
	@DeleteMapping("/{publicId}")
	public OperationStatusResponse deleteUser(@PathVariable String publicId) {
		
		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		userService.deleteUser(publicId);
		
		operationStatus.setOperationName(OperationName.DELETE_USER.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("The user has been deleted successfully");
		
		return operationStatus;
	}
	
	@PostMapping("/password-reset/request")
	public ResponseEntity<OperationStatusResponse> passwordResetRequest(
			@Valid @RequestBody PasswordResetRequestModel passwordResetRequest) {

		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		userService.passwordResetRequest(passwordResetRequest);
		
		operationStatus.setOperationName(OperationName.PASSWORD_RESET_REQUEST.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("Password reset link has sent to your email addess successfully");
		
		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
	
	@GetMapping("/password-reset/validate")
	public ResponseEntity<OperationStatusResponse> validatePasswordResetToken(@RequestParam String token) {

		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		userService.validatePasswordResetToken(token);
		
		operationStatus.setOperationName(OperationName.PASSWORD_RESET_TOKEN_VALIDATION.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("Password reset token has been verified successfully");
		
		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
	
	@PostMapping("/password-reset/confirm")
	public ResponseEntity<OperationStatusResponse> passwordResetConfirmation(
			@Valid @RequestBody PasswordResetConfirmationRequest passwordResetConfirmation) {

		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		userService.resetPassword(passwordResetConfirmation);
		
		operationStatus.setOperationName(OperationName.PASSWORD_RESET_CONFIRMATION.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("Your password has been reset successfully.");
		
		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
	
	@PutMapping("/me/change-password")
	public ResponseEntity<OperationStatusResponse> changePassword(
			@Valid @RequestBody ChangePasswordRequestModel changePasswordRequest,
			@AuthenticationPrincipal UserPrincipal userPrincipal) {
		
		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		userService.changePassword(userPrincipal, changePasswordRequest);
		
		operationStatus.setOperationName(OperationName.CHANGE_PASSWORD.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("Your password has been successfully updated.");
		
		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
	
	@GetMapping("/{publicId}/posts")
	public ResponseEntity<PageResponseModel<PostResponseModel>> getUserPosts(@PathVariable String publicId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "createdAt") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir
			){
		
		return new ResponseEntity<>(userService.getUserPosts(publicId, page, pageSize, sortBy, sortDir), HttpStatus.OK);
	}
}
