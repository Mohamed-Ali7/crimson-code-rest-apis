package com.crimson_code_blog_rest_apis.services;

import com.crimson_code_blog_rest_apis.dto.request.TagRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.TagResponseModel;

public interface TagService {

	TagResponseModel createTag(TagRequestModel tagRequest);
	TagResponseModel getTag(long tagId);
	PageResponseModel<TagResponseModel> getAllTags(int page, int pageSize, String sortBy, String sortDir);
	TagResponseModel updateTag(long tagId, TagRequestModel tagRequest);
	void deleteTag(long id);
	PageResponseModel<PostResponseModel> getTagPosts(String tagName, int page, int pageSize,
			String sortBy, String sortDir);
}
