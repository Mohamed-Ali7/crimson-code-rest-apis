package com.crimson_code_blog_rest_apis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crimson_code_blog_rest_apis.dto.request.TagRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.OperationStatusResponse;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.TagResponseModel;
import com.crimson_code_blog_rest_apis.exceptions.ErrorResponse;
import com.crimson_code_blog_rest_apis.services.TagService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/tags")
@Tag(name = "Tags APIs", description = "Endpoints for managing blog post tags")
public class TagController {

	private TagService tagService;

	@Autowired
	public TagController(TagService tagService) {
		this.tagService = tagService;
	}
	
	@PostMapping
	@Operation(
			summary = "Create a new tag",
	        description = "Creates a new tag. Only the tag name is required.",
	        responses = {
	        		@ApiResponse(responseCode = "201", description = "Tag created successfully"),
	                @ApiResponse(responseCode = "400", description = "Invalid request payload",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	                @ApiResponse(responseCode = "401", description = "User is not authenticated",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<TagResponseModel> createTag(@Valid @RequestBody TagRequestModel tagRequest) {
		return new ResponseEntity<>(tagService.createTag(tagRequest), HttpStatus.CREATED);
	}
	
	@GetMapping("/{tagId}")
	@Operation(
			summary = "Get tag by ID",
	        description = "Retrieves a tag using its unique identifier",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Tag retrieved successfully"),
	                @ApiResponse(responseCode = "404", description = "Tag not found",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	public ResponseEntity<TagResponseModel> getTag(@PathVariable long tagId) {
		return new ResponseEntity<>(tagService.getTag(tagId), HttpStatus.OK);
	}
	
	@GetMapping
	@Operation(
			summary = "Get all tags (paginated)",
	        description = "Returns a paginated list of all tags sorted by name",
	        responses = {
	            @ApiResponse(responseCode = "200", description = "Tags retrieved successfully")
	        }
	)
	public ResponseEntity<PageResponseModel<TagResponseModel>> getAllTags(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "name") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir) {
		return new ResponseEntity<>(tagService.getAllTags(page, pageSize, sortBy, sortDir), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{tagId}")
	@Operation(
			summary = "Update an existing tag",
	        description = "Updates a tag's name. Only accessible by admins.",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Tag updated successfully"),
	                @ApiResponse(responseCode = "400", description = "Invalid request payload",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	                @ApiResponse(responseCode = "404", description = "tag not found",
	            		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                ),
	                @ApiResponse(responseCode = "401", description = "User is not authenticated",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	                @ApiResponse(responseCode = "403", description = "Forbidden. Admin role required",
	                	content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                )}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<TagResponseModel> updateTag(@PathVariable long tagId,
			@Valid @RequestBody TagRequestModel tagRequest) {
		
		return new ResponseEntity<>(tagService.updateTag(tagId, tagRequest), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{tagId}")
	@Operation(
			summary = "Delete a tag",
	        description = "Deletes a tag by its ID. Only accessible by admins.",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Tag deleted successfully"),
	                @ApiResponse(responseCode = "404", description = "tag not found",
	            		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                ),
	                @ApiResponse(responseCode = "401", description = "User is not authenticated",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	                @ApiResponse(responseCode = "403", description = "Forbidden. Admin role required",
	                	content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                )}
	)
	@SecurityRequirement(name = "bearerAuth")
	public OperationStatusResponse deleteTag(@PathVariable long tagId) {
		
		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		tagService.deleteTag(tagId);
		
		operationStatus.setOperationName(OperationName.DELETE_TAG.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("The tag has been deleted successfully");
		
		return operationStatus;
	}
	
	@GetMapping("/{tagName}/posts")
	@Operation(
			summary = "Get posts by tag name",
	        description = "Returns all posts associated with a specific tag, paginated and sortable",
	        responses = {
	            @ApiResponse(responseCode = "200", description = "Posts page retrieved successfully"),
	            @ApiResponse(responseCode = "404", description = "Tag not found",
	            		content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        )}
	)
	public ResponseEntity<PageResponseModel<PostResponseModel>> getTagPosts(@PathVariable String tagName,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "createdAt") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir
			){
		
		return new ResponseEntity<>(
				tagService.getTagPosts(tagName, page, pageSize, sortBy, sortDir), HttpStatus.OK);
	}
	
}
