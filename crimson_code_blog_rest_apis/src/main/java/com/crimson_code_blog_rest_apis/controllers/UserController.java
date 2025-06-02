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
import com.crimson_code_blog_rest_apis.exceptions.ErrorResponse;
import com.crimson_code_blog_rest_apis.security.UserPrincipal;
import com.crimson_code_blog_rest_apis.services.UserService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users APIs", description = "Endpoints for managing user profiles and information")
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PutMapping("/me/profile-picture")
	@Operation(
			summary = "Update profile picture",
	        description = "Uploads a new profile picture for the currently authenticated user",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Profile picture updated successfully"),
	                @ApiResponse(responseCode = "400", description = "Invalid image file",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	                @ApiResponse(responseCode = "401", description = "User is not authenticated",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	@SecurityRequirement(name = "bearerAuth")
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
	@Operation(
			summary = "Get current user",
	        description = "Retrieves details of the currently authenticated user",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "User retrieved successfully"),
	        		@ApiResponse(responseCode = "401", description = "User is not authenticated",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)
	        }
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<UserResponseModel> getCurrentUser( @AuthenticationPrincipal UserPrincipal userPrincipal) {
		return new ResponseEntity<>(userService.getCurrentUser(userPrincipal), HttpStatus.OK);
	}

	@GetMapping("/{publicId}")
	@Operation(
			summary = "Get user by public ID",
	        description = "Retrieves a user by their public identifier",
	        responses = {
	            @ApiResponse(responseCode = "200", description = "User Retrieved successfully"),
	        		@ApiResponse(responseCode = "404", description = "User not found",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)
	        }
	)
	public ResponseEntity<UserResponseModel> getUser(@PathVariable String publicId) {
		return new ResponseEntity<>(userService.getUser(publicId), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	@Operation(
			summary = "Get all users (admin only)",
			description = "Returns a paginated list of all users. Admin access required.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
					@ApiResponse(responseCode = "403", description = "Forbidden (Not an admin)",
		        		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
					)
	        }
	)
	@SecurityRequirement(name = "bearerAuth")
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
	@Operation(
			summary = "Update user details",
	        description = "Allows a user to update their profile. Only the user themself can perform this operation.",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "User updated successfully"),
	        		@ApiResponse(responseCode = "404", description = "User not found",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	        		@ApiResponse(responseCode = "401", description = "User is not authenticated",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
					@ApiResponse(responseCode = "403", description = "Forbidden (User is not the owner of the profile or an admin)",
		        		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
					)
	        }
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<UserResponseModel> updateUser(@PathVariable String publicId,
			@Valid @RequestBody UpdateUserRequestModel updateRequest) {
		
		return new ResponseEntity<>(userService.updateUser(publicId, updateRequest), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN') or principal.publicId == #publicId")
	@DeleteMapping("/{publicId}")
	@Operation(
			summary = "Delete user",
	        description = "Deletes a user account. Can be performed by the user or an admin.",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "User deleted successfully"),
	        		@ApiResponse(responseCode = "404", description = "User not found",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	        		@ApiResponse(responseCode = "401", description = "User is not authenticated",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
					@ApiResponse(responseCode = "403", description = "Forbidden (User is not the owner of the profile or an admin)",
		        		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
					),
					@ApiResponse(responseCode = "400", description = "User has Admin role",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
					)
	        }
	)
	@SecurityRequirement(name = "bearerAuth")
	public OperationStatusResponse deleteUser(@PathVariable String publicId) {
		
		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		userService.deleteUser(publicId);
		
		operationStatus.setOperationName(OperationName.DELETE_USER.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("The user has been deleted successfully");
		
		return operationStatus;
	}
	
	@PostMapping("/password-reset/request")
	@Operation(
			summary = "Request password reset",
	        description = "Sends a password reset link to the user's email address",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Password reset link sent successfully"),
	        		@ApiResponse(responseCode = "404", description = "User not found",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)
	        }
	)
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
	@Operation(
			summary = "Validate password reset token",
	        description = "Validates the token used for password reset",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Token is valid"),
	        		@ApiResponse(responseCode = "400", description = "Token is invalid or expired",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)
	        }
	)
	public ResponseEntity<OperationStatusResponse> validatePasswordResetToken(@RequestParam String token) {

		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		userService.validatePasswordResetToken(token);
		
		operationStatus.setOperationName(OperationName.PASSWORD_RESET_TOKEN_VALIDATION.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("Password reset token has been verified successfully");
		
		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
	
	@PostMapping("/password-reset/confirm")
	@Operation(
			summary = "Confirm password reset",
	        description = "Resets the user's password using the valid token",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Password reset successfully"),
	        		@ApiResponse(responseCode = "400",
	        			description = "Invalid request payload or token or The new password and confirm password do not match",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)
	        }
	)
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
	@Operation(
			summary = "Change current user password",
	        description = "Allows the currently authenticated user to change their password",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Password changed successfully"),
	        		@ApiResponse(responseCode = "401", description = "User is not authenticated",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
					@ApiResponse(responseCode = "400",
						description = "The current password is incorrect or the new password and confirm password do not match",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
					)
	        }
	)
	@SecurityRequirement(name = "bearerAuth")
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
	@Operation(
			summary = "Get posts by user",
	        description = "Returns a paginated list of posts created by a specific user",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Posts page retrieved successfully"),
	        		@ApiResponse(responseCode = "404", description = "User not found",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)
	        }
	)
	public ResponseEntity<PageResponseModel<PostResponseModel>> getUserPosts(@PathVariable String publicId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "createdAt") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir
			){
		
		return new ResponseEntity<>(userService.getUserPosts(publicId, page, pageSize, sortBy, sortDir), HttpStatus.OK);
	}
}
