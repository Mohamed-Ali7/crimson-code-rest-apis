package com.crimson_code_blog_rest_apis.services.impl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.crimson_code_blog_rest_apis.dto.request.CommentRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.CommentResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserResponseModel;
import com.crimson_code_blog_rest_apis.entity.CommentEntity;
import com.crimson_code_blog_rest_apis.entity.PostEntity;
import com.crimson_code_blog_rest_apis.entity.UserEntity;
import com.crimson_code_blog_rest_apis.exceptions.CrimsonCodeGlobalException;
import com.crimson_code_blog_rest_apis.exceptions.ResourceNotFoundException;
import com.crimson_code_blog_rest_apis.repository.CommentRepository;
import com.crimson_code_blog_rest_apis.repository.PostRepository;
import com.crimson_code_blog_rest_apis.repository.UserRepository;
import com.crimson_code_blog_rest_apis.security.UserPrincipal;
import com.crimson_code_blog_rest_apis.services.CommentService;
import com.crimson_code_blog_rest_apis.utils.UserRoles;

@Service
public class CommentServiceImpl implements CommentService {

	private PostRepository postRepository;
	private CommentRepository commentRepository;
	private UserRepository userRepository;
	private ModelMapper modelMapper;
	
	@Autowired
	public CommentServiceImpl(PostRepository postRepository, CommentRepository commentRepository,
			UserRepository userRepository, ModelMapper modelMapper) {
		this.postRepository = postRepository;
		this.commentRepository = commentRepository;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public CommentResponseModel createComment(long postId, CommentRequestModel commentRequest) {
		PostEntity postEntity = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post does not exist with id: " + postId));
		
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		
		UserEntity currentUser = userRepository.findByEmail(userEmail)
				.orElseThrow(() ->
						new ResourceNotFoundException("User does not exist with email: " + userEmail));
		
		CommentEntity commentEntity = new CommentEntity();
		
		commentEntity.setContent(commentRequest.getContent());
		
		OffsetDateTime creationDate = OffsetDateTime.now(ZoneOffset.UTC);
		
		commentEntity.setCreatedAt(creationDate);
		commentEntity.setUpdatedAt(creationDate);
		commentEntity.setUserPublicId(currentUser.getPublicId());
		commentEntity.setUser(currentUser);
		commentEntity.setPost(postEntity);
		
		CommentEntity savedComment = commentRepository.save(commentEntity);
		
		CommentResponseModel commentResponse = mapToCommentResponse(savedComment);
		
		return commentResponse;
	}

	@Override
	public CommentResponseModel getComment(long postId, long commentId) {

		PostEntity postEntity = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post does not exist with id: " + postId));
		
		CommentEntity commentEntity = commentRepository.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment does not exist with id: " + postId));
		
		if (commentEntity.getPost().getId() != postEntity.getId()) {
			throw new CrimsonCodeGlobalException("There is no such comment for this post");
		}
		
		CommentResponseModel commentResponse = mapToCommentResponse(commentEntity);

		return commentResponse;
	}

	@Override
	public PageResponseModel<CommentResponseModel> getAllComments(long postId, int page, int pageSize, String sortBy,
			String sortDir) {

		page = page > 0 ? page - 1 : page;
		
		Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		
		Page<CommentEntity> commentsPage = commentRepository.findAllByPostId(postId, pageable);
		
		List<CommentEntity> comments = commentsPage.getContent();
		
		List<CommentResponseModel> commentsResponse = comments.stream()
				.map(comment -> mapToCommentResponse(comment)).collect(Collectors.toList());
		
		PageResponseModel<CommentResponseModel> pageResponse = new PageResponseModel<>();
		
		pageResponse.setContent(commentsResponse);
		pageResponse.setPageNumber(++page);
		pageResponse.setPageSize(commentsPage.getNumberOfElements());
		pageResponse.setTotalElements(commentsPage.getTotalElements());
		pageResponse.setTotalPages(commentsPage.getTotalPages());
		pageResponse.setIsLast(commentsPage.isLast());
		
		return pageResponse;
	}

	@Override
	public CommentResponseModel updateComment(long postId, long commentId, CommentRequestModel commentRequest) {
		
		PostEntity postEntity = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post does not exist with id: " + postId));
		
		CommentEntity commentEntity = commentRepository.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment does not exist with id: " + postId));
		
		if (commentEntity.getPost().getId() != postEntity.getId()) {
			throw new CrimsonCodeGlobalException("There is no such comment for this post");
		}
		
		UserPrincipal userPrincipal = 
				(UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		boolean isAdmin = userPrincipal.getAuthorities()
				.contains(new SimpleGrantedAuthority(UserRoles.ROLE_ADMIN.name()));
		
		if (!userPrincipal.getPublicId().equals(commentEntity.getUserPublicId()) && !isAdmin) {
			throw new AccessDeniedException(
					"UNAUTHORIZED: User " + userPrincipal.getUsername() + " is not authorized to update this Comment");
		}
		
		commentEntity.setContent(commentRequest.getContent());
		commentEntity.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
		
		CommentEntity updatedComment = commentRepository.save(commentEntity);
		
		CommentResponseModel commentResponse = mapToCommentResponse(updatedComment);
		
		return commentResponse;
	}

	@Override
	public void deleteComment(long postId, long commentId) {
		PostEntity postEntity = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post does not exist with id: " + postId));
		
		CommentEntity commentEntity = commentRepository.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment does not exist with id: " + postId));
		
		if (commentEntity.getPost().getId() != postEntity.getId()) {
			throw new CrimsonCodeGlobalException("There is no such comment for this post");
		}
		
		UserPrincipal userPrincipal = 
				(UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		boolean isAdmin = userPrincipal.getAuthorities()
				.contains(new SimpleGrantedAuthority(UserRoles.ROLE_ADMIN.name()));
		
		if (!userPrincipal.getPublicId().equals(commentEntity.getUserPublicId()) && !isAdmin) {
			throw new AccessDeniedException(
					"UNAUTHORIZED: User " + userPrincipal.getUsername() + " is not authorized to delete this Comment");
		}
		
		commentRepository.delete(commentEntity);
	}

	private CommentResponseModel mapToCommentResponse(CommentEntity commentEntity) {

		CommentResponseModel commentResponse = new CommentResponseModel();

		commentResponse.setId(commentEntity.getId());
		commentResponse.setUser(modelMapper.map(commentEntity.getUser(), UserResponseModel.class));
		commentResponse.setContent(commentEntity.getContent());
		commentResponse.setCreatedAt(commentEntity.getCreatedAt());
		commentResponse.setUpdatedAt(commentEntity.getUpdatedAt());

		return commentResponse;
	}
}
