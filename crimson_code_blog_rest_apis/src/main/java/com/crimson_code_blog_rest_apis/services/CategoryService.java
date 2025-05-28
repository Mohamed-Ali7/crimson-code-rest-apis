package com.crimson_code_blog_rest_apis.services;

import com.crimson_code_blog_rest_apis.dto.request.CategoryRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.CategoryResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;

public interface CategoryService {

	CategoryResponseModel createCategory(CategoryRequestModel categoryRequest);
	CategoryResponseModel getCategory(long id);
	PageResponseModel<CategoryResponseModel> getAllCategories(int page, int pageSize, String sortBy, String sortDir);
	CategoryResponseModel updateCategory(long id, CategoryRequestModel categoryRequest);
	void deleteCategory(long categoryId);
	PageResponseModel<PostResponseModel> getCategoryPosts(String categoryName, int page, int pageSize,
			String sortBy, String sortDir);
}
