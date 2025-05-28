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
import com.crimson_code_blog_rest_apis.services.TagService;
import com.crimson_code_blog_rest_apis.utils.OperationName;
import com.crimson_code_blog_rest_apis.utils.OperationStatus;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/tags")
public class TagController {

	private TagService tagService;

	@Autowired
	public TagController(TagService tagService) {
		this.tagService = tagService;
	}
	
	@PostMapping
	public ResponseEntity<TagResponseModel> createTag(@Valid @RequestBody TagRequestModel tagRequest) {
		return new ResponseEntity<>(tagService.createTag(tagRequest), HttpStatus.CREATED);
	}
	
	@GetMapping("/{tagId}")
	public ResponseEntity<TagResponseModel> getTag(@PathVariable long tagId) {
		return new ResponseEntity<>(tagService.getTag(tagId), HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<PageResponseModel<TagResponseModel>> getAllTags(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "15") int pageSize,
			@RequestParam(name = "sort_by", defaultValue = "name") String sortBy,
			@RequestParam(name = "sort_dir", defaultValue = "asc") String sortDir) {
		return new ResponseEntity<>(tagService.getAllTags(page, pageSize, sortBy, sortDir), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{tagId}")
	public ResponseEntity<TagResponseModel> updateTag(@PathVariable long tagId,
			@Valid @RequestBody TagRequestModel tagRequest) {
		
		return new ResponseEntity<>(tagService.updateTag(tagId, tagRequest), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{tagId}")
	public OperationStatusResponse deleteTag(@PathVariable long tagId) {
		
		OperationStatusResponse operationStatus = new OperationStatusResponse();
		
		tagService.deleteTag(tagId);
		
		operationStatus.setOperationName(OperationName.DELETE_TAG.name());
		
		operationStatus.setOperationStatus(OperationStatus.SUCCESS.name());
		
		operationStatus.setMessage("The tag has been deleted successfully");
		
		return operationStatus;
	}
	
	@GetMapping("/{tagName}/posts")
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
