package com.crimson_code_blog_rest_apis.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crimson_code_blog_rest_apis.dto.request.PostRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.OperationStatusResponse;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;
import com.crimson_code_blog_rest_apis.security.UserPrincipal;
import com.crimson_code_blog_rest_apis.services.PostService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	private PostService postService;

	@Autowired
	public PostController(PostService postService) {
		this.postService = postService;
	}
	
	/*
	 * This request accept multipart/form-data media type because the request sends
	 * JSON body which is the post data and at the same time it sends
	 * a file which is the post image of the post
	 * 
	 * Note that the client has to explicitly provide the content type of the post input as application/json
	 */
	
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<PostResponseModel> createPost(
			@Valid @RequestPart("post") PostRequestModel postRequest,
			@RequestPart(value = "postImage", required = false) MultipartFile postImage) {
		
		return new ResponseEntity<>(postService.createPost(postRequest, postImage), HttpStatus.CREATED);
	}
	
	@GetMapping("/{postId}")
	public ResponseEntity<PostResponseModel> getPost(@PathVariable long postId) {
		return new ResponseEntity<>(postService.getPost(postId), HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<PageResponseModel<PostResponseModel>> getAllPosts(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "createdAt") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "desc") String sortDir
			){
		
		return new ResponseEntity<>(postService.getAllPosts(page, pageSize, sortBy, sortDir), HttpStatus.OK);
	}
	
	@GetMapping("/search")
	public ResponseEntity<PageResponseModel<PostResponseModel>> searchPosts(
			@RequestParam(name = "query", defaultValue = "") String searchQuery,
			@RequestParam(name = "tags", defaultValue = "") List<String> tags,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "createdAt") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "desc") String sortDir
			){
		
		return new ResponseEntity<>(
				postService.searchPosts(searchQuery, tags, page, pageSize, sortBy, sortDir), HttpStatus.OK);
	}
	
	@PutMapping(value = "/{postId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<PostResponseModel> updatePost(
			@PathVariable long postId,
			@AuthenticationPrincipal UserPrincipal userPrincipal,
			@Valid @RequestPart("post") PostRequestModel postRequest,
			@RequestPart(value = "postImage", required = false) MultipartFile postImage) {
		
		return new ResponseEntity<>(postService.updatePost(postId, postRequest, postImage, userPrincipal), HttpStatus.OK);
	}
	
	
	@DeleteMapping("/{postId}")
	public ResponseEntity<OperationStatusResponse> deletePost(@PathVariable long postId,
			@AuthenticationPrincipal UserPrincipal userPrincipal) {

		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		postService.deletePost(postId, userPrincipal);
		
		operationStatus.setOperationName(OperationName.DELETE_POST.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("The post has been deleted successfully");
		
		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
}
