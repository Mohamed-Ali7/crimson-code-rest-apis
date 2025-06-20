package com.crimson_code_blog_rest_apis.services.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.crimson_code_blog_rest_apis.dto.response.FollowingStatusResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.PageResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserSummaryResponseModel;
import com.crimson_code_blog_rest_apis.entity.UserEntity;
import com.crimson_code_blog_rest_apis.exceptions.CrimsonCodeGlobalException;
import com.crimson_code_blog_rest_apis.exceptions.ResourceNotFoundException;
import com.crimson_code_blog_rest_apis.repository.UserRepository;
import com.crimson_code_blog_rest_apis.security.UserPrincipal;
import com.crimson_code_blog_rest_apis.services.FollowService;

import jakarta.transaction.Transactional;

@Service
public class FollowServiceImpl implements FollowService {
	
	private UserRepository userRepository;
	private ModelMapper modelMapper;
	
	@Autowired
	public FollowServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public void follow(String targetUserId, UserPrincipal authenticatedUser) {

		UserEntity userToFollow = userRepository.findByPublicId(targetUserId).
				orElseThrow(() -> new ResourceNotFoundException("User does not exist with id: " + targetUserId));

		String currentUserId = authenticatedUser.getPublicId();
		UserEntity currentUser = userRepository.findByPublicId(currentUserId).
				orElseThrow(() -> new ResourceNotFoundException("User does not exist with id: " + currentUserId));
		
		if (currentUser.equals(userToFollow)) {
			throw new CrimsonCodeGlobalException("You cannot follow yourself.");
		}
		
		boolean isFollowing = userRepository.isFollowing(currentUser.getId(), userToFollow.getId());
		
		if (isFollowing) {
			throw new CrimsonCodeGlobalException("You are already following this user.");
		}
		
		currentUser.addFollowing(userToFollow);
		userRepository.save(currentUser);
	}
	

	@Override
	@Transactional
	public void unFollow(String targetUserId, UserPrincipal authenticatedUser) {
		UserEntity userToFollow = userRepository.findByPublicId(targetUserId).
				orElseThrow(() -> new ResourceNotFoundException("User does not exist with id: " + targetUserId));

		String currentUserId = authenticatedUser.getPublicId();
		UserEntity currentUser = userRepository.findByPublicId(currentUserId).
				orElseThrow(() -> new ResourceNotFoundException("User does not exist with id: " + currentUserId));
		
		userRepository.unfollowUser(currentUser.getId(), userToFollow.getId());

	}

	@Override
	public PageResponseModel<UserSummaryResponseModel> followers(String targetUserId, int page, int size,
			String sortBy, String sortDir) {
		
		UserEntity userToFollow = userRepository.findByPublicId(targetUserId).
				orElseThrow(() -> new ResourceNotFoundException("User does not exist with id: " + targetUserId));

		page = page > 0 ? page - 1 : page;
		
		Sort sort = sortDir.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		Pageable pageable = PageRequest.of(page, size, sort);
		
		Page<UserSummaryResponseModel> followersPage = userRepository.findUserFollowers(userToFollow.getId(), pageable);
		
		return buildPageResponse(followersPage, page);
	}

	@Override
	public PageResponseModel<UserSummaryResponseModel> following(String targetUserId, int page, int size,
			String sortBy, String sortDir) {
		
		UserEntity userToFollow = userRepository.findByPublicId(targetUserId).
				orElseThrow(() -> new ResourceNotFoundException("User does not exist with id: " + targetUserId));

		page = page > 0 ? page - 1 : page;
		
		Sort sort = sortDir.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		Pageable pageable = PageRequest.of(page, size, sort);
		
		Page<UserSummaryResponseModel> followingsPage = userRepository.findUserFollowings(userToFollow.getId(), pageable);
		
		return buildPageResponse(followingsPage, page);
	}

	@Override
	public FollowingStatusResponseModel followingStatus(String targetUserId, UserPrincipal authenticatedUser) {

		
		UserEntity userToFollow = userRepository.findByPublicId(targetUserId).
				orElseThrow(() -> new ResourceNotFoundException("User does not exist with id: " + targetUserId));

		if (authenticatedUser == null || authenticatedUser.getUserEntity() == null) {
		    throw new AccessDeniedException("Full authentication is required to access this resource");
		}

		UserEntity currentUser = authenticatedUser.getUserEntity();

		boolean isFollowing = userRepository.isFollowing(currentUser.getId(), userToFollow.getId());
		
		return new FollowingStatusResponseModel(isFollowing);
	}
	
	private PageResponseModel<UserSummaryResponseModel> buildPageResponse(Page<UserSummaryResponseModel> currentPage, int page) {
		
		List<UserSummaryResponseModel> responseContent = currentPage.getContent();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null && !(authentication.getPrincipal() instanceof String)) {
			UserPrincipal authenticatedUser = (UserPrincipal) authentication.getPrincipal();
			
			Set<String> followingIds = userRepository.followingIds(authenticatedUser.getUserEntity().getId());
			
			//Check if the followers of the profile owner are followed by the authenticated user or not
			responseContent.forEach(user -> user.setIsFollowing(
					followingIds.contains(user.getPublicId())
					));
			
		}
		
		PageResponseModel<UserSummaryResponseModel> pageResponse = new PageResponseModel<>();
		
		pageResponse.setContent(responseContent);
		pageResponse.setPageNumber(++page);
		pageResponse.setPageSize(currentPage.getNumberOfElements());
		pageResponse.setTotalElements(currentPage.getTotalElements());
		pageResponse.setTotalPages(currentPage.getTotalPages());
		pageResponse.setIsLast(currentPage.isLast());
		
		return pageResponse;
	}

}
