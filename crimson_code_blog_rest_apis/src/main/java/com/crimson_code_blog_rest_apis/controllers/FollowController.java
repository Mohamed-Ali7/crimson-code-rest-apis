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
import com.crimson_code_blog_rest_apis.security.UserPrincipal;
import com.crimson_code_blog_rest_apis.services.FollowService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

@RestController
@RequestMapping("/api/users/{targetUserId}")
public class FollowController {

	private FollowService followService;

	@Autowired
	public FollowController(FollowService followService) {
		this.followService = followService;
	}

	@PostMapping("/follow")
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
	public ResponseEntity<FollowingStatusResponseModel> followingStatus(@PathVariable String targetUserId,
			@AuthenticationPrincipal UserPrincipal authenticatedUser) {
		
		return new ResponseEntity<>(followService.followingStatus(targetUserId, authenticatedUser), HttpStatus.OK);
	}
}
