package com.crimson_code_blog_rest_apis.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.crimson_code_blog_rest_apis.dto.request.PostRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;
import com.crimson_code_blog_rest_apis.security.UserPrincipal;

public interface PostService {

	PostResponseModel createPost(PostRequestModel postRequest, MultipartFile postImage);
	PostResponseModel getPost(long postId);
	PageResponseModel<PostResponseModel> getAllPosts(int page, int pageSize, String sortBy, String sortDir);
	PostResponseModel updatePost(long postId, PostRequestModel postRequest, MultipartFile postImage,
			UserPrincipal userPrincipal);

	void deletePost(long postId, UserPrincipal userPrincipal);
	PageResponseModel <PostResponseModel>searchPosts(String searchQuery, List<String> tags, int page,
			int pageSize, String sortBy, String sortDir);
}
