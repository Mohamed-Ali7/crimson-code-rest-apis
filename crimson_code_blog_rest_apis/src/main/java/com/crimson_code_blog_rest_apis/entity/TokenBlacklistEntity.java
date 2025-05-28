package com.crimson_code_blog_rest_apis.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="token_blacklist")
public class TokenBlacklistEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "token", nullable = false, columnDefinition = "text")
	private String token;
	
	@Column(name = "token_type", nullable = false, length=25)
	private String tokenType;
	
	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;
	
	public TokenBlacklistEntity() {
		
	}
	
	public TokenBlacklistEntity(String token, String tokenType, LocalDateTime expiresAt) {
		this.token = token;
		this.tokenType = tokenType;
		this.expiresAt = expiresAt;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
	
}
