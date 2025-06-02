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
import com.crimson_code_blog_rest_apis.exceptions.ErrorResponse;
import com.crimson_code_blog_rest_apis.services.CommentService;
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
@RequestMapping("/api/posts/{postId}/comments")
@Tag(name = "Comments APIs", description = "Endpoints for managing blog post comments")
public class CommentController {
	
	private CommentService commentService;

	@Autowired
	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}
	
	@PostMapping
	@Operation(
			summary = "Create a comment on a post",
	        description = "Adds a new comment to the specified blog post",
	        responses = {
	        		@ApiResponse(responseCode = "201", description = "Comment created successfully"),
	                @ApiResponse(responseCode = "400", description = "Invalid request payload",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	                @ApiResponse(responseCode = "404", description = "Post not found",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                ),
	                @ApiResponse(responseCode = "401", description = "User is not authenticated",
        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<CommentResponseModel> createComment(@PathVariable long postId,
			@RequestBody CommentRequestModel commentRequest) {
		
		return new ResponseEntity<>(commentService.createComment(postId, commentRequest), HttpStatus.CREATED);
	}
	
	@GetMapping("/{commentId}")
	@Operation(
			summary = "Get a comment by ID",
	        description = "Retrieve a specific comment by its ID under the given post",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Comment retrieved successfully"),
	                @ApiResponse(responseCode = "404", description = "Comment or Post not found",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                )}
	)
	public ResponseEntity<CommentResponseModel> getComment(@PathVariable long postId, @PathVariable long commentId) {
		return new ResponseEntity<>(commentService.getComment(postId, commentId), HttpStatus.OK);
	}
	
	@GetMapping
	@Operation(
			summary = "Get all comments of a post",
	        description = "Retrieve paginated comments of a specific blog post",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Comments page retrieved successfully"),
	                @ApiResponse(responseCode = "404", description = "Post not found",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                )}
	)
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
	@Operation(
			summary = "Update a comment (Comment author and Admin only)",
	        description = "Update an existing comment by its ID under a given post",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Comment updated successfully"),
	        		@ApiResponse(responseCode = "400", description = "Invalid request payload",
		        		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	        		@ApiResponse(responseCode = "404", description = "Comment or Post not found",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	        		@ApiResponse(responseCode = "401", description = "User is not authenticated",
        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	        		@ApiResponse(responseCode = "403", description = "User is not authorized (not the author of the comment)",
        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<CommentResponseModel> updateComment(@PathVariable long postId,
			@PathVariable long commentId, @Valid @RequestBody CommentRequestModel commentRequest) {
		
		return new ResponseEntity<>(commentService.updateComment(postId, commentId, commentRequest), HttpStatus.OK);
	}
	
	@DeleteMapping("/{commentId}")
	@Operation(
			summary = "Delete a comment (Comment author and Admin only)",
	        description = "Delete a comment by its ID under the given post",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
	        		@ApiResponse(responseCode = "404", description = "Comment or Post not found",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	        		@ApiResponse(responseCode = "401", description = "User is not authenticated",
        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	        		@ApiResponse(responseCode = "403", description = "User is not authorized (not the author of the comment)",
        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<OperationStatusResponse> deleteComment(@PathVariable long postId, @PathVariable long commentId) {
		
		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		commentService.deleteComment(postId, commentId);
		
		operationStatus.setOperationName(OperationName.DELETE_COMMENT.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("The comment has been deleted successfully");
		
		return new ResponseEntity<>(operationStatus, HttpStatus.OK);
	}
	

}
