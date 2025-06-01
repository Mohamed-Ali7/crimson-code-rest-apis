package com.crimson_code_blog_rest_apis.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
import org.springframework.web.multipart.MultipartFile;

import com.crimson_code_blog_rest_apis.dto.request.PostRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.CategoryResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.TagResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserSummaryResponseModel;
import com.crimson_code_blog_rest_apis.entity.CategoryEntity;
import com.crimson_code_blog_rest_apis.entity.PostEntity;
import com.crimson_code_blog_rest_apis.entity.TagEntity;
import com.crimson_code_blog_rest_apis.entity.UserEntity;
import com.crimson_code_blog_rest_apis.exceptions.CrimsonCodeGlobalException;
import com.crimson_code_blog_rest_apis.exceptions.ResourceNotFoundException;
import com.crimson_code_blog_rest_apis.repository.CategoryRepository;
import com.crimson_code_blog_rest_apis.repository.CommentRepository;
import com.crimson_code_blog_rest_apis.repository.PostRepository;
import com.crimson_code_blog_rest_apis.repository.TagRepository;
import com.crimson_code_blog_rest_apis.repository.UserRepository;
import com.crimson_code_blog_rest_apis.security.UserPrincipal;
import com.crimson_code_blog_rest_apis.services.CloudinaryService;
import com.crimson_code_blog_rest_apis.services.PostService;
import com.crimson_code_blog_rest_apis.utils.UserRoles;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class PostServiceImpl implements PostService {
	
	private PostRepository postRepository;
	private UserRepository userRepository;
	private CategoryRepository categoryRepository;
	private TagRepository tagRepository;
	private static ModelMapper modelMapper;
	private CommentRepository commentRepository;
	private CloudinaryService cloudinaryService;
	
	private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

	@Autowired
	public PostServiceImpl(PostRepository postRepository, UserRepository userRepository,
			CategoryRepository categoryRepository, TagRepository tagRepository, ModelMapper modelMapper,
			CommentRepository commentRepository, CloudinaryService cloudinaryService) {
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.categoryRepository = categoryRepository;
		this.tagRepository = tagRepository;
		PostServiceImpl.modelMapper = modelMapper;
		this.commentRepository = commentRepository;
		this.cloudinaryService = cloudinaryService;
	}

	@Override
	public PostResponseModel createPost(PostRequestModel postRequest, MultipartFile postImage) {
		
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		
		UserEntity userEntity = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User does not exist with email: " + userEmail));
	
		CategoryEntity categoryEntity = categoryRepository.findById(postRequest.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Category does not exist with id: " + postRequest.getCategoryId()));
		
		PostEntity newPost = new PostEntity();
		
		OffsetDateTime postCreationDate = OffsetDateTime.now(ZoneOffset.UTC);
		
		newPost.setTitle(postRequest.getTitle());
		newPost.setContent(postRequest.getContent());
		newPost.setCreatedAt(postCreationDate);
		newPost.setUpdatedAt(postCreationDate);
		newPost.setUserPublicId(userEntity.getPublicId());
		newPost.setUser(userEntity);
		newPost.setCategory(categoryEntity);
		
		if (postRequest.getTags() != null) {
			postRequest.getTags().forEach(tag -> {
				TagEntity tagEntity = tagRepository.findByNameIgnoreCase(tag)
						.orElseGet(() -> tagRepository.save(new TagEntity(tag)));
				newPost.addTag(tagEntity);
			});
		}
		
		PostEntity savedPost = postRepository.save(newPost);
		
		// Saving post image
		if (postImage != null && !postImage.isEmpty()) {
			try {
				String thumbnailPublicId = "post_" + savedPost.getId() + "_thumbnail";
				String imageUrl = cloudinaryService.uploadFile(postImage, "post_thumbnails", thumbnailPublicId);
				savedPost.setImageUrl(imageUrl);
				postRepository.save(savedPost);
			} catch (IOException ex) {
				log.warn("Post thumbnail upload failed. Proceeding without thumbnail.");
			}
		}
		
		PostResponseModel postResponse = mapToPostResponse(savedPost);
		
		if (savedPost.getTags() != null) {
			List<TagResponseModel> tagsResponse = savedPost.getTags().stream()
					.map(tag -> new TagResponseModel(tag.getId(), tag.getName())).collect(Collectors.toList());
			postResponse.setTags(tagsResponse);
		} else {
			postResponse.setTags(Collections.emptyList());
		}
		
		
		return postResponse;
	}

	@Override
	public PostResponseModel getPost(long postId) {

		PostEntity postEntity = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post does not exist with id: " + postId));
		
		PostResponseModel postResponse = mapToPostResponse(postEntity);
		
		return postResponse;
	}

	@Override
	public PageResponseModel<PostResponseModel> getAllPosts(int page, int pageSize, String sortBy, String sortDir) {
		page = page > 0 ? page - 1 : page; // To make pages start from 1 not 0 as it's more user-friendly
		
		Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		
		Page<PostEntity> postsPage = postRepository.findAll(pageable);
		
		return buildPostsPage(postsPage, page);
	}

	@Override
	public PageResponseModel<PostResponseModel> searchPosts(String searchQuery, List<String> tags,
			int page, int pageSize, String sortBy, String sortDir) {

		page = page > 0 ? page - 1 : page;
		
		Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		
		tags = tags.stream().map(tag -> tag.toLowerCase()).toList();
		
		Page<PostEntity> postsPage;
		
		if (searchQuery.isBlank() && tags.isEmpty()) {
			return getAllPosts(page, pageSize, sortBy, sortDir);
		} else if (searchQuery.isBlank()) {
			postsPage = postRepository.searchByTags(tags, pageable);
		} else {
			postsPage = postRepository.searchByTitleOrTags(searchQuery, tags, pageable);
		}
		
		return buildPostsPage(postsPage, page);
	}
	
	@Override
	public PostResponseModel updatePost(long postId, PostRequestModel postRequest, MultipartFile postImage,
			UserPrincipal userPrincipal) {

		PostEntity postEntity = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post does not exist with id: " + postId));
		
		boolean isAdmin = userPrincipal.getAuthorities()
				.contains(new SimpleGrantedAuthority(UserRoles.ROLE_ADMIN.name()));
		
		
		if (!userPrincipal.getPublicId().equals(postEntity.getUserPublicId()) && !isAdmin) {
			throw new AccessDeniedException(
					"UNAUTHORIZED: User " + userPrincipal.getUsername() + " is not authorized to update this post");
		}
		
		postEntity.setTitle(postRequest.getTitle());
		postEntity.setContent(postRequest.getContent());
		
		if (postEntity.getCategory().getId() != postRequest.getCategoryId()) {
			CategoryEntity categoryEntity = categoryRepository.findById(postRequest.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Category does not exist with id: " + postRequest.getCategoryId()));
			
			postEntity.setCategory(categoryEntity);
		}
		
		postEntity.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
		
		
		if (postImage != null && !postImage.isEmpty()) {
			try {
				String thumbnailPublicId = "post_" + postEntity.getId() + "_thumbnail";
				String imageUrl = cloudinaryService.uploadFile(postImage, "post_thumbnails", thumbnailPublicId);
				postEntity.setImageUrl(imageUrl);
			} catch (IOException ex) {
				log.warn("Post thumbnail upload failed. Proceeding without thumbnail.");
			}
		}
		
		if (postRequest.getTags() != null) {
			
			List<String> requestTags = postRequest.getTags();
			
			List<TagEntity> existedTagEntities = tagRepository.findAllByNameIn(requestTags);
			
			List<String> existedTagNames = existedTagEntities.stream()
					.map(tag -> tag.getName().toLowerCase()).collect(Collectors.toList());
			
			List <TagEntity> newTagEntities = requestTags.stream()
					.filter(requestTag -> !existedTagNames.contains(requestTag.toLowerCase()))
					.map(requestTag -> new TagEntity(requestTag.toLowerCase()))
					.collect(Collectors.toList());

			List<TagEntity> savedNewTagEntities = tagRepository.saveAll(newTagEntities);
			
			List <TagEntity> allPostTags = new ArrayList<>();
			
			allPostTags.addAll(existedTagEntities);
			allPostTags.addAll(savedNewTagEntities);
			
			postEntity.setTags(allPostTags);
		}
		
		PostEntity updatedPost = postRepository.save(postEntity);
		PostResponseModel postResponse = mapToPostResponse(updatedPost);
		
		return postResponse;
	}

	@Override
	@Transactional
	public void deletePost(long postId, UserPrincipal userPrincipal) {
		PostEntity postEntity = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post does not exist with id: " + postId));
		
		boolean isAdmin = userPrincipal.getAuthorities()
				.contains(new SimpleGrantedAuthority(UserRoles.ROLE_ADMIN.name()));
		
		
		if (!userPrincipal.getPublicId().equals(postEntity.getUserPublicId()) && !isAdmin) {
			throw new AccessDeniedException(
					"UNAUTHORIZED: User " + userPrincipal.getUsername() + " is not authorized to delete this post");
		}

		commentRepository.deleteByPostId(postId);
		postRepository.delete(postEntity);
		
		if (postEntity.getImageUrl() != null) {
			String tumbnailId = "post_" + postEntity.getId() + "_thumbnail";
			cloudinaryService.deleteFile(tumbnailId);
	    }
	}

	private PageResponseModel<PostResponseModel> buildPostsPage(Page<PostEntity> postsPage, int page) {
		
		List<PostEntity> posts = postsPage.getContent();
		
		List<PostResponseModel> postsResponse = posts.stream()
				.map(post -> mapToPostResponse(post)).collect(Collectors.toList());
		
		PageResponseModel<PostResponseModel> pageResponse = new PageResponseModel<>();
		
		pageResponse.setContent(postsResponse);
		pageResponse.setPageNumber(++page);
		pageResponse.setPageSize(postsPage.getNumberOfElements());
		pageResponse.setTotalElements(postsPage.getTotalElements());
		pageResponse.setTotalPages(postsPage.getTotalPages());
		pageResponse.setIsLast(postsPage.isLast());
		
		return pageResponse;
	}
	
	protected static PostResponseModel mapToPostResponse(PostEntity postEntity) {
		
		PostResponseModel postResponse = new PostResponseModel();
		
		postResponse.setId(postEntity.getId());
		postResponse.setTitle(postEntity.getTitle());
		postResponse.setContent(postEntity.getContent());
		postResponse.setImageUrl(postEntity.getImageUrl());
		postResponse.setUser(modelMapper.map(postEntity.getUser(), UserSummaryResponseModel.class));
		postResponse.setCreatedAt(postEntity.getCreatedAt());
		postResponse.setUpdatedAt(postEntity.getUpdatedAt());
		postResponse.setCategory(modelMapper.map(postEntity.getCategory(), CategoryResponseModel.class));
		
		if (postEntity.getTags() != null) {
			List<TagResponseModel> postTags = postEntity.getTags().stream()
					.map(tag -> new TagResponseModel(tag.getId(), tag.getName())).collect(Collectors.toList());
			postResponse.setTags(postTags);
		} else {
			postResponse.setTags(Collections.emptyList());
		}
		
		return postResponse;
	}
}
