package com.crimson_code_blog_rest_apis.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import java.lang.reflect.Type;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crimson_code_blog_rest_apis.dto.request.CategoryRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.CategoryResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;
import com.crimson_code_blog_rest_apis.entity.CategoryEntity;
import com.crimson_code_blog_rest_apis.entity.PostEntity;
import com.crimson_code_blog_rest_apis.exceptions.CrimsonCodeGlobalException;
import com.crimson_code_blog_rest_apis.exceptions.ResourceNotFoundException;
import com.crimson_code_blog_rest_apis.repository.CategoryRepository;
import com.crimson_code_blog_rest_apis.repository.PostRepository;
import com.crimson_code_blog_rest_apis.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	private CategoryRepository categoryRepository;
	private ModelMapper modelMapper;
	private PostRepository postRepository;
	
	@Autowired
	public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper,
			PostRepository postRepository) {
		this.categoryRepository = categoryRepository;
		this.modelMapper = modelMapper;
		this.postRepository = postRepository;
	}

	@Override
	public CategoryResponseModel createCategory(CategoryRequestModel categoryRequest) {
		Optional<CategoryEntity> category = categoryRepository.findByNameIgnoreCase(categoryRequest.getName());
		
		if (category.isPresent()) {
			throw new CrimsonCodeGlobalException("This category already exists");
		}
		
		CategoryEntity newCategory = modelMapper.map(categoryRequest, CategoryEntity.class);

		CategoryEntity savedCategory = categoryRepository.save(newCategory);
		
		CategoryResponseModel categoryResponse = modelMapper.map(savedCategory, CategoryResponseModel.class);
		
		return categoryResponse;
	}

	@Override
	public CategoryResponseModel getCategory(long id) {

		CategoryEntity categoryEntity = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category does not exist with id: " + id));
		
		CategoryResponseModel categoryResponse = modelMapper.map(categoryEntity, CategoryResponseModel.class);
		
		return categoryResponse;
	}

	@Override
	public PageResponseModel<CategoryResponseModel> getAllCategories(int page, int pageSize,
			String sortBy, String sortDir) {

		page = page > 0 ? page - 1: page; // To make pages start from 1 not 0 as it's more user-friendly
		
		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		
		Page<CategoryEntity> categoriesPage = categoryRepository.findAll(pageable);
		
		List<CategoryEntity> categories = categoriesPage.getContent();
		
		Type typeList = new TypeToken<List<CategoryResponseModel>>() {}.getType();
		
		List<CategoryResponseModel> categoriesResponse = modelMapper.map(categories, typeList);
		
		PageResponseModel<CategoryResponseModel> pageResponse = new PageResponseModel<>();
		
		pageResponse.setContent(categoriesResponse);
		pageResponse.setPageNumber(++page);
		pageResponse.setPageSize(categoriesPage.getNumberOfElements());
		pageResponse.setTotalElements(categoriesPage.getTotalElements());
		pageResponse.setTotalPages(categoriesPage.getTotalPages());
		pageResponse.setIsLast(categoriesPage.isLast());
		
		return pageResponse;
	}

	@Override
	public CategoryResponseModel updateCategory(long id, CategoryRequestModel categoryRequest) {

		CategoryEntity categoryEntity = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category does not exist with id: " + id));
		
		categoryEntity.setName(categoryRequest.getName());
		
		CategoryEntity updatedCategory = categoryRepository.save(categoryEntity);
		
		CategoryResponseModel categoryResponse = modelMapper.map(updatedCategory, CategoryResponseModel.class);
		
		return categoryResponse;
	}

	@Override
	public void deleteCategory(long id) {

		CategoryEntity categoryEntity = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category does not exist with id: " + id));
		
		categoryRepository.delete(categoryEntity);
		
	}

	@Override
	public PageResponseModel<PostResponseModel> getCategoryPosts(String categoryName, int page, int pageSize, String sortBy,
			String sortDir) {

		CategoryEntity categoryEntity = categoryRepository.findByNameIgnoreCase(categoryName)
				.orElseThrow(() -> new ResourceNotFoundException("Category does not exist with name: " + categoryName));
		
		page = page > 0 ? page - 1 : page; // To make pages start from 1 not 0 as it's more user-friendly
		
		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		
		Page<PostEntity> categoryPostsPage = postRepository.findAllByCategoryNameIgnoreCase(categoryEntity.getName(), pageable);
		
		List<PostEntity> categoryPosts = categoryPostsPage.getContent();
		
		List<PostResponseModel> categoryPostsResponse = categoryPosts.stream()
				.map(post -> PostServiceImpl.mapToPostResponse(post))
				.collect(Collectors.toList());
		
		PageResponseModel<PostResponseModel> pageResponse = new PageResponseModel<>();
		
		pageResponse.setContent(categoryPostsResponse);
		pageResponse.setPageNumber(++page);
		pageResponse.setPageSize(categoryPostsPage.getNumberOfElements());
		pageResponse.setTotalElements(categoryPostsPage.getTotalElements());
		pageResponse.setTotalPages(categoryPostsPage.getTotalPages());
		pageResponse.setIsLast(categoryPostsPage.isLast());
		
		return pageResponse;
	}

}
