package com.crimson_code_blog_rest_apis.utils;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crimson_code_blog_rest_apis.exceptions.JwtTokenException;
import com.crimson_code_blog_rest_apis.repository.TokenBlacklistRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtils {

	@Value("${emailVerificationTokenExpirationDate}")
	private long emailVerificationTokenExpirationDate;
	
	@Value("${accessTokenExpirationDate}")
	private long accessTokenExpirationDate;

	@Value("${refreshTokenExpirationDate}")
	private long refreshTokenExpirationDate;
	
	@Value("${passwordResetTokenExpirationDate}")
	private long passwordResetTokenExpirationDate;
	@Value("${tokenSecert}")
	private String secretKey;
	
	private TokenBlacklistRepository tokenBlacklistRepository;
	
	public JwtUtils() {
		
	}
	
	@Autowired
	public JwtUtils(TokenBlacklistRepository tokenBlacklistRepository) {
		this.tokenBlacklistRepository = tokenBlacklistRepository;
	}

	public String generateEmailVerificationToken(String username) {
		return generateJwtToken(username, null, emailVerificationTokenExpirationDate);
	}
	
	public String generateAccessToken(String username, Map<String, Object> claims) {
		return generateJwtToken(username, claims, accessTokenExpirationDate);
	}
	
	public String generateRefreshToken(String username) {
		return generateJwtToken(username, null, refreshTokenExpirationDate);
	}
	
	public String generatePasswordResetToken(String username) {
		return generateJwtToken(username, null, passwordResetTokenExpirationDate);
	}
	
	private String generateJwtToken(String subject, Map<String, Object> claims, long expirationDate) {
		JwtBuilder jwt = Jwts.builder();
		
		jwt.subject(subject)
		.expiration(new Date(System.currentTimeMillis() + expirationDate))
		.signWith(key())
		.issuedAt(new Date());
		
		if (claims != null) {
			jwt.claims(claims);
		}
		
		return jwt.compact();
	}
	
	public String extractUsername(String token) {
		return getClaim(token, (claims) -> claims.getSubject());
	}
	
	public Date extractExpirationDate(String token) {
		return getClaim(token, (claims) -> claims.getExpiration());
	}
	
	public <T> T getClaim (String token, Function<Claims, T> claimResolver) {
		Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		
		return Jwts.parser()
				.verifyWith(key())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
	public void validateJwtToken(String token, JwtTokenType tokenType) {
		
		try {
			Jwts.parser()
			.verifyWith(key())
			.build()
			.parse(token);
		} catch (ExpiredJwtException ex) {
			throw new JwtTokenException(tokenType, tokenType.getValue() + " Token has expired");
		} catch (Exception ex) {
			throw new JwtTokenException(tokenType, "Invalid " + tokenType.getValue() + " token - " + ex.getMessage());
		}
	}

	private SecretKey key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}
	
	public boolean tokenIsBlacklisted(String token) {
		return tokenBlacklistRepository.existsByToken(token);
	}
}
