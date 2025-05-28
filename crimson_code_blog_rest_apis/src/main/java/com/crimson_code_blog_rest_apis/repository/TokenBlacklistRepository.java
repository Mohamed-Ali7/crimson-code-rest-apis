package com.crimson_code_blog_rest_apis.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crimson_code_blog_rest_apis.entity.TokenBlacklistEntity;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntity, Long> {

	boolean existsByToken(String token);
	void deleteAllByExpiresAtBefore(LocalDateTime now);
}
