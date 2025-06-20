package com.crimson_code_blog_rest_apis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crimson_code_blog_rest_apis.dto.response.FollowingStatusResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.OperationStatusResponse;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserSummaryResponseModel;
import com.crimson_code_blog_rest_apis.exceptions.ErrorResponse;
import com.crimson_code_blog_rest_apis.security.UserPrincipal;
import com.crimson_code_blog_rest_apis.services.FollowService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users/{targetUserId}")
@Tag(
		name = "Follow APIs",
		description = "Endpoints for following and unfollowing users, checking follow status, and retrieving followers/following lists."
		)
public class FollowController {

	private FollowService followService;

	@Autowired
	public FollowController(FollowService followService) {
		this.followService = followService;
	}

	@PostMapping("/follow")
	@Operation(
			summary = "Follow a user",
	        description = "Allows the authenticated user to follow the specified user by their ID.",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "User followed successfully"),
	                @ApiResponse(responseCode = "404", description = "User to follow not found",
	            		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                ),
	                @ApiResponse(responseCode = "401", description = "User is not authenticated",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<OperationStatusResponse> follow(@PathVariable String targetUserId,
			@AuthenticationPrincipal UserPrincipal authenticatedUser) {

		OperationStatusResponse operationStatus = new OperationStatusResponse();

		followService.follow(targetUserId, authenticatedUser);

		operationStatus.setOperationName(OperationName.FOLLOW_USER.name());

		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());

		operationStatus.setMessage("Followed the user successfully.");

		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
	
	@DeleteMapping("/follow")
	@Operation(
			summary = "Unfollow a user",
	        description = "Allows the authenticated user to unfollow the specified user",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "User unfollowed successfully"),
	                @ApiResponse(responseCode = "404", description = "Target user not found",
	            		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                ),
	                @ApiResponse(responseCode = "401", description = "User is not authenticated",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<OperationStatusResponse> unFollow(@PathVariable String targetUserId,
			@AuthenticationPrincipal UserPrincipal authenticatedUser) {

		OperationStatusResponse operationStatus = new OperationStatusResponse();

		followService.unFollow(targetUserId, authenticatedUser);

		operationStatus.setOperationName(OperationName.UNFOLLOW_USER.name());

		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());

		operationStatus.setMessage("Unfollowing the user successfully.");

		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
	
	@GetMapping("/followers")
	@Operation(
	        summary = "Get followers",
	        description = "Retrieves a paginated list of users who follow the specified user.",
	        responses = {
	            @ApiResponse(responseCode = "200", description = "List of followers retrieved successfully."),
                @ApiResponse(responseCode = "404", description = "Target user not found",
            		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                )
	        }
	    )
	public ResponseEntity<PageResponseModel<UserSummaryResponseModel>> getFollowers(
			@PathVariable String targetUserId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "firstName") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir
			){
		
		return new ResponseEntity<>(
				followService.followers(targetUserId,page, pageSize, sortBy, sortDir),
				HttpStatus.OK);
	}
	
	@GetMapping("/following")
	@Operation(
	        summary = "Get following",
	        description = "\"Retrieves a paginated list of users that the specified user is following.",
	        responses = {
	            @ApiResponse(responseCode = "200", description = "List of followed users retrieved successfully."),
                @ApiResponse(responseCode = "404", description = "Target user not found",
            		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                )
	        }
	    )
	public ResponseEntity<PageResponseModel<UserSummaryResponseModel>> getFollowing(
			@PathVariable String targetUserId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "firstName") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir
			){
		
		return new ResponseEntity<>(
				followService.following(targetUserId,page, pageSize, sortBy, sortDir),
				HttpStatus.OK);
	}
	
	@GetMapping("/follow/status")
	@Operation(
			summary = "Check follow status",
	        description = "Checks if the authenticated user is currently following the specified user",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Follow status retrieved successfully"),
	                @ApiResponse(responseCode = "404", description = "Target user not found",
	            		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                ),
	                @ApiResponse(responseCode = "401", description = "User is not authenticated",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<FollowingStatusResponseModel> followingStatus(@PathVariable String targetUserId,
			@AuthenticationPrincipal UserPrincipal authenticatedUser) {
		
		return new ResponseEntity<>(followService.followingStatus(targetUserId, authenticatedUser), HttpStatus.OK);
	}
}
