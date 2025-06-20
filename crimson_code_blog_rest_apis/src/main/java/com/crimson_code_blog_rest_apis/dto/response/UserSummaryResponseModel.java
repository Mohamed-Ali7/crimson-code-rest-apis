package com.crimson_code_blog_rest_apis.dto.response;

public class UserSummaryResponseModel extends FollowingStatusResponseModel {

	private String publicId;
	private String firstName;
	private String lastName;
	private String profileImgUrl;
	
	public UserSummaryResponseModel() {
		
	}

	public UserSummaryResponseModel(String publicId, String firstName, String lastName, String profileImgUrl) {
		this.publicId = publicId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.profileImgUrl = profileImgUrl;
	}

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getProfileImgUrl() {
		return profileImgUrl;
	}

	public void setProfileImgUrl(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}
	
	
}
