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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crimson_code_blog_rest_apis.dto.request.CategoryRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.CategoryResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.OperationStatusResponse;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;
import com.crimson_code_blog_rest_apis.exceptions.ErrorResponse;
import com.crimson_code_blog_rest_apis.services.CategoryService;
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
@RequestMapping("/api/categories")
@Tag(name = "Categories APIs", description = "Endpoints for managing blog post categories")
public class CategoryController {

	private CategoryService categoryService;
	
	@Autowired
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	@Operation(
			summary = "Create a new category",
	        description = "Allows admin to create a new blog category",
	        responses = {
	        		@ApiResponse(responseCode = "201", description = "Category created successfully"),
	                @ApiResponse(responseCode = "400", description = "Category name already exists or Invalid request payload",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	                @ApiResponse(responseCode = "403", description = "User does not have Admin role",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                )}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<CategoryResponseModel> createCategory(
			@Valid @RequestBody CategoryRequestModel categoryRequest) {

		return new ResponseEntity<>(categoryService.createCategory(categoryRequest), HttpStatus.CREATED);
	}
	
	@GetMapping("/{categoryId}")
	@Operation(
			summary = "Get a category by ID",
			description = "Fetch a specific blog category using its ID",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Category found"),
	        		@ApiResponse(responseCode = "404", description = "Category not found",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		)}
	)
	public ResponseEntity<CategoryResponseModel> getCategory(@PathVariable long categoryId) {
		return new ResponseEntity<>(categoryService.getCategory(categoryId), HttpStatus.OK);
	}
	
	@GetMapping
	@Operation(
			summary = "Get all categories",
	        description = "Retrieve all categories with pagination and sorting",
	        responses = {@ApiResponse(responseCode = "200", description = "Categories page retrieved successfully")}
	)
	public ResponseEntity<PageResponseModel<CategoryResponseModel>> getAllCategories(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "name") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir) {
		
		return new ResponseEntity<>(categoryService.getAllCategories(page, pageSize, sortBy, sortDir), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{categoryId}")
	@Operation(
			summary = "Update a category",
	        description = "Allows admin to update an existing category",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Category updated successfully"),
	                @ApiResponse(responseCode = "400", description = "Invalid request payload",
	        			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	        		),
	                @ApiResponse(responseCode = "404", description = "Category not found",
    					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                ),
	                @ApiResponse(responseCode = "403", description = "User does not have Admin role",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                )}
	)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<CategoryResponseModel> updateCategory(@PathVariable long categoryId,
			@Valid @RequestBody CategoryRequestModel categoryRequest) {
		
		return new ResponseEntity<>(categoryService.updateCategory(categoryId, categoryRequest), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{categoryId}")
	@Operation(
			summary = "Delete a category",
	        description = "Allows admin to delete an existing category",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Category deleted successfully"),
	                @ApiResponse(responseCode = "404", description = "Category not found",
    					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                ),
	                @ApiResponse(responseCode = "403", description = "User does not have Admin role",
        				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                )}
	)
	@SecurityRequirement(name = "bearerAuth")
	public OperationStatusResponse deleteCategory(@PathVariable long categoryId) {
		
		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		categoryService.deleteCategory(categoryId);
		
		operationStatus.setOperationName(OperationName.DELETE_CATEGORY.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("The category has been deleted successfully");
		
		return operationStatus;
	}
	
	@GetMapping("/{categoryName}/posts")
	@Operation(
			summary = "Get posts under a category",
	        description = "Retrieve all posts associated with a specific category name, with pagination and sorting",
	        responses = {
	        		@ApiResponse(responseCode = "200", description = "Category Posts page retrieved successfully"),
	                @ApiResponse(responseCode = "404", description = "Category not found",
    					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
	                )}
	)
	public ResponseEntity<PageResponseModel<PostResponseModel>> getCategoryPosts(@PathVariable String categoryName,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "createdAt") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir
			){
		
		return new ResponseEntity<>(
				categoryService.getCategoryPosts(categoryName, page, pageSize, sortBy, sortDir), HttpStatus.OK
				);
	}
}
