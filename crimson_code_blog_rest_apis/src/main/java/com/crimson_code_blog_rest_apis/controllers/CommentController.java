package com.crimson_code_blog_rest_apis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crimson_code_blog_rest_apis.dto.request.CommentRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.CommentResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.OperationStatusResponse;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.services.CommentService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
	
	private CommentService commentService;

	@Autowired
	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}
	
	@PostMapping
	public ResponseEntity<CommentResponseModel> createComment(@PathVariable long postId,
			@RequestBody CommentRequestModel commentRequest) {
		
		return new ResponseEntity<>(commentService.createComment(postId, commentRequest), HttpStatus.CREATED);
	}
	
	@GetMapping("/{commentId}")
	public ResponseEntity<CommentResponseModel> getComment(@PathVariable long postId, @PathVariable long commentId) {
		return new ResponseEntity<>(commentService.getComment(postId, commentId), HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<PageResponseModel<CommentResponseModel>> getAllPostComments(
			@PathVariable long postId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "15") int pageSize,
			@RequestParam(value = "sort_by", defaultValue = "createdAt") String sortBy,
			@RequestParam(value = "sort_dir", defaultValue = "desc") String sortDir) {

		return new ResponseEntity<>(
				commentService.getAllComments(postId, page, pageSize, sortBy, sortDir), HttpStatus.OK
				);
	}
	
	@PutMapping("/{commentId}")
	public ResponseEntity<CommentResponseModel> updateComment(@PathVariable long postId,
			@PathVariable long commentId, @Valid @RequestBody CommentRequestModel commentRequest) {
		
		return new ResponseEntity<>(commentService.updateComment(postId, commentId, commentRequest), HttpStatus.OK);
	}
	
	@DeleteMapping("/{commentId}")
	public ResponseEntity<OperationStatusResponse> deleteComment(@PathVariable long postId, @PathVariable long commentId) {
		
		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		commentService.deleteComment(postId, commentId);
		
		operationStatus.setOperationName(OperationName.DELETE_COMMENT.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("The comment has been deleted successfully");
		
		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
	

}
