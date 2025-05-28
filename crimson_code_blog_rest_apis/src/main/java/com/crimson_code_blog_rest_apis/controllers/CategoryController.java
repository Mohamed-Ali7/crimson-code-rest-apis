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
import com.crimson_code_blog_rest_apis.services.CategoryService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private CategoryService categoryService;
	
	@Autowired
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<CategoryResponseModel> createCategory(
			@Valid @RequestBody CategoryRequestModel categoryRequest) {

		return new ResponseEntity<>(categoryService.createCategory(categoryRequest), HttpStatus.CREATED);
	}
	
	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryResponseModel> getCategory(@PathVariable long categoryId) {
		return new ResponseEntity<>(categoryService.getCategory(categoryId), HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<PageResponseModel<CategoryResponseModel>> getAllCategories(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "name") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir) {
		
		return new ResponseEntity<>(categoryService.getAllCategories(page, pageSize, sortBy, sortDir), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryResponseModel> updateCategory(@PathVariable long categoryId,
			@Valid @RequestBody CategoryRequestModel categoryRequest) {
		
		return new ResponseEntity<>(categoryService.updateCategory(categoryId, categoryRequest), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{categoryId}")
	public OperationStatusResponse deleteCategory(@PathVariable long categoryId) {
		
		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		categoryService.deleteCategory(categoryId);
		
		operationStatus.setOperationName(OperationName.DELETE_CATEGORY.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("The category has been deleted successfully");
		
		return operationStatus;
	}
	
	@GetMapping("/{categoryName}/posts")
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
