package com.crimson_code_blog_rest_apis;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.crimson_code_blog_rest_apis.repository.TokenBlacklistRepository;

import jakarta.transaction.Transactional;

@Service
@EnableScheduling
public class Scheduling {

	private TokenBlacklistRepository tokenBlacklistRepository;

	@Autowired
	public Scheduling(TokenBlacklistRepository tokenBlacklistRepository) {
		this.tokenBlacklistRepository = tokenBlacklistRepository;
	}

	@Scheduled(cron = "0 0 * * * *" ) //every hour
	@Transactional
	void revokeExpiredTokenFronBlacklist() {
		tokenBlacklistRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
	}
}
