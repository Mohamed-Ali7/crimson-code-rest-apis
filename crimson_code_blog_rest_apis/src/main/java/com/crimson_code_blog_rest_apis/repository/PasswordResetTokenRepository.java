package com.crimson_code_blog_rest_apis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crimson_code_blog_rest_apis.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {

	Optional<PasswordResetTokenEntity> findByToken(String token);
	
	Optional<PasswordResetTokenEntity> findByUserId(long id);
	
	void deleteByUserId(long userId);
}
