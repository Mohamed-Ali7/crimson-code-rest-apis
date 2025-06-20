package com.crimson_code_blog_rest_apis.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column(name = "public_id", unique = true, nullable = false, length = 60)
	String publicId;

	@Column(name = "email", unique = true, nullable = false, length = 150)
	String email;

	@Column(name = "password", nullable = false, length = 100)
	String password;

	@Column(name = "first_name", nullable = false, length = 50)
	String firstName;

	@Column(name = "last_name", length = 50)
	String lastName;

	@Column(name = "joined_at", nullable = false, columnDefinition = "TIMESTAMP")
	private OffsetDateTime joinedAt;
	
	@Column(name = "profile_img_url")
	String profileImgUrl;
	
	@Column(name = "email_verification_token", nullable = true, length = 250)
	private String emailVerificationToken;
	
	@Column(name = "is_email_verified", nullable = false)
	private boolean isEmailVerified = false;

	@ManyToMany(fetch = FetchType.LAZY, cascade = {
			CascadeType.DETACH,
			CascadeType.MERGE,
			CascadeType.REFRESH
	})
	@JoinTable(name = "users_roles",
	joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), 
	inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
	)
	List<RoleEntity> roles;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
			CascadeType.DETACH,
			CascadeType.MERGE,
			CascadeType.REFRESH,
			CascadeType.PERSIST
	})
	private List<PostEntity> posts;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
			CascadeType.DETACH,
			CascadeType.MERGE,
			CascadeType.REFRESH,
			CascadeType.PERSIST
	})
	private List<CommentEntity> comments;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {
			CascadeType.DETACH,
			CascadeType.MERGE,
			CascadeType.REFRESH
	})
	
	@JoinTable(name = "user_followings",
			joinColumns = @JoinColumn(name = "follower_id"),
			inverseJoinColumns = @JoinColumn(name = "following_id"))
	Set<UserEntity> followings;
	
	@ManyToMany(mappedBy = "followings")
	Set<UserEntity> followers;
	
	public UserEntity() {

	}

	public UserEntity(String publicId, String email, String password, String firstName, String lastName,
			OffsetDateTime joinedAt, String profileImgUrl) {
		this.publicId = publicId;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.joinedAt = joinedAt;
		this.profileImgUrl = profileImgUrl;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public OffsetDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(OffsetDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}
	
	public String getProfileImgUrl() {
		return profileImgUrl;
	}

	public void setProfileImgUrl(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}

	public String getEmailVerificationToken() {
		return emailVerificationToken;
	}

	public void setEmailVerificationToken(String emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}

	public boolean getIsEmailVerified() {
		return isEmailVerified;
	}

	public void setIsEmailVerified(boolean isEmailVerified) {
		this.isEmailVerified = isEmailVerified;
	}

	public List<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleEntity> roles) {
		this.roles = roles;
	}

	public void addRole(RoleEntity role) {
		if (this.roles == null) {
			this.roles = new ArrayList<>();
		}

		this.roles.add(role);
	}

	public List<PostEntity> getPosts() {
		return posts;
	}

	public void setPosts(List<PostEntity> posts) {
		this.posts = posts;
	}

	public List<CommentEntity> getComments() {
		return comments;
	}

	public void setComments(List<CommentEntity> comments) {
		this.comments = comments;
	}

	public Set<UserEntity> getFollowings() {
		return followings;
	}

	public void setFollowings(Set<UserEntity> followings) {
		this.followings = followings;
	}

	public Set<UserEntity> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<UserEntity> followers) {
		this.followers = followers;
	}
	
	public void addFollowing(UserEntity userToFollow) {
		if (this.followings == null) {
			followings = new HashSet<>();
		}
		
		followings.add(userToFollow);
	}
	
	@Override
	public boolean equals(Object o) {
		
	    if (this == o) {
	    	return true;
	    }
	    
	    if (o == null || getClass() != o.getClass()) {
	    	return false;
	    }

	    UserEntity that = (UserEntity) o;

	    return this.id == that.id;
	}
}
