package com.crimson_code_blog_rest_apis.services;

import com.crimson_code_blog_rest_apis.dto.request.CommentRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.CommentResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;

public interface CommentService {

	CommentResponseModel createComment(long postId, CommentRequestModel commentRequest);
	CommentResponseModel getComment(long postId, long commentId);
	PageResponseModel<CommentResponseModel> getAllComments(long postId, int page, int pageSize,
			String sortBy, String sortDir);
	CommentResponseModel updateComment(long postId, long commentId, CommentRequestModel commentRequest);
	void deleteComment(long postId, long commentId);
}
