package com.crimson_code_blog_rest_apis.services.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crimson_code_blog_rest_apis.dto.request.TagRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PostResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.TagResponseModel;
import com.crimson_code_blog_rest_apis.entity.PostEntity;
import com.crimson_code_blog_rest_apis.entity.TagEntity;
import com.crimson_code_blog_rest_apis.exceptions.ResourceNotFoundException;
import com.crimson_code_blog_rest_apis.repository.PostRepository;
import com.crimson_code_blog_rest_apis.repository.TagRepository;
import com.crimson_code_blog_rest_apis.services.TagService;

@Service
public class TagServiceImpl implements TagService {

	private TagRepository tagRepository;
	private ModelMapper modelMapper;
	private PostRepository postRepository;
	
	@Autowired
	public TagServiceImpl(TagRepository tagRepository, ModelMapper modelMapper, PostRepository postRepository) {
		this.tagRepository = tagRepository;
		this.modelMapper = modelMapper;
		this.postRepository = postRepository;
	}

	@Override
	public TagResponseModel createTag(TagRequestModel tagRequest) {

		TagEntity newTag = tagRepository.findByNameIgnoreCase(tagRequest.getName())
				.orElseGet(() -> tagRepository.save(new TagEntity(tagRequest.getName())));
		
		TagResponseModel tagResponse = modelMapper.map(newTag, TagResponseModel.class);
		
		return tagResponse;
	}

	@Override
	public TagResponseModel getTag(long tagId) {
		TagEntity tagEntity = tagRepository.findById(tagId)
				.orElseThrow(() -> new ResourceNotFoundException("Tag does not exist with id: " + tagId));
		
		TagResponseModel tagResponse = modelMapper.map(tagEntity, TagResponseModel.class);
		
		return tagResponse;
	}

	@Override
	public PageResponseModel<TagResponseModel> getAllTags(int page, int pageSize, String sortBy, String sortDir) {
		page = page > 0 ? page - 1: page; // To make pages start from 1 not 0 as it's more user-friendly
		
		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		
		Page<TagEntity> tagsPage = tagRepository.findAll(pageable);
		
		List<TagEntity> tags = tagsPage.getContent();
		
		Type typeList = new TypeToken<List<TagResponseModel>>() {}.getType();
		
		List<TagResponseModel> tagssResponse = modelMapper.map(tags, typeList);
		
		PageResponseModel<TagResponseModel> pageResponse = new PageResponseModel<>();
		
		pageResponse.setContent(tagssResponse);
		pageResponse.setPageNumber(++page);
		pageResponse.setPageSize(tagsPage.getNumberOfElements());
		pageResponse.setTotalElements(tagsPage.getTotalElements());
		pageResponse.setTotalPages(tagsPage.getTotalPages());
		pageResponse.setIsLast(tagsPage.isLast());
		
		return pageResponse;
	}

	@Override
	public TagResponseModel updateTag(long tagId, TagRequestModel tagRequest) {
		TagEntity tagEntity = tagRepository.findById(tagId)
				.orElseThrow(() -> new ResourceNotFoundException("Tag does not exist with id: " + tagId));
		
		tagEntity.setName(tagRequest.getName());
		
		TagEntity updatedTag = tagRepository.save(tagEntity);
		
		TagResponseModel tagResponse = modelMapper.map(updatedTag, TagResponseModel.class);
		
		return tagResponse;
	}

	@Override
	public void deleteTag(long tagId) {
		TagEntity tagEntity = tagRepository.findById(tagId)
				.orElseThrow(() -> new ResourceNotFoundException("Tag does not exist with id: " + tagId));
		
		tagRepository.delete(tagEntity);
	}

	@Override
	public PageResponseModel<PostResponseModel> getTagPosts(String tagName, int page, int pageSize, String sortBy,
			String sortDir) {

		TagEntity tagEntity = tagRepository.findByNameIgnoreCase(tagName)
				.orElseThrow(() -> new ResourceNotFoundException("Tag does not exist with name: " + tagName));
		
		page = page > 0 ? page - 1 : page; // To make pages start from 1 not 0 as it's more user-friendly
		
		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		
		Page<PostEntity> tagPostsPage = postRepository.findAllByTagName(tagEntity.getName(), pageable);
		
		List<PostEntity> tagPosts = tagPostsPage.getContent();
		
		List<PostResponseModel> tagPostsResponse = tagPosts.stream()
				.map(post -> PostServiceImpl.mapToPostResponse(post))
				.collect(Collectors.toList());
		
		PageResponseModel<PostResponseModel> pageResponse = new PageResponseModel<>();
		
		pageResponse.setContent(tagPostsResponse);
		pageResponse.setPageNumber(++page);
		pageResponse.setPageSize(tagPostsPage.getNumberOfElements());
		pageResponse.setTotalElements(tagPostsPage.getTotalElements());
		pageResponse.setTotalPages(tagPostsPage.getTotalPages());
		pageResponse.setIsLast(tagPostsPage.isLast());
		
		return pageResponse;
	}

}
