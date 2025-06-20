package com.crimson_code_blog_rest_apis.dto.response;

public class FollowingStatusResponseModel {

	private boolean isFollowing;

	public FollowingStatusResponseModel() {
	}

	public FollowingStatusResponseModel(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}

	public boolean getIsFollowing() {
		return isFollowing;
	}

	public void setIsFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}
	
}
