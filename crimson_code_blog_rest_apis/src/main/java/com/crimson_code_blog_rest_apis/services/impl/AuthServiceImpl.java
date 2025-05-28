package com.crimson_code_blog_rest_apis.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crimson_code_blog_rest_apis.security.UserPrincipal;
import com.crimson_code_blog_rest_apis.dto.request.EmailVerificationRequest;
import com.crimson_code_blog_rest_apis.dto.request.LoginRequestModel;
import com.crimson_code_blog_rest_apis.dto.request.LogoutRequestModel;
import com.crimson_code_blog_rest_apis.dto.request.RegisterRequestModel;
import com.crimson_code_blog_rest_apis.dto.response.LoginResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.RefreshTokenResponseModel;
import com.crimson_code_blog_rest_apis.dto.response.UserResponseModel;
import com.crimson_code_blog_rest_apis.entity.RoleEntity;
import com.crimson_code_blog_rest_apis.entity.TokenBlacklistEntity;
import com.crimson_code_blog_rest_apis.entity.UserEntity;
import com.crimson_code_blog_rest_apis.exceptions.CrimsonCodeGlobalException;
import com.crimson_code_blog_rest_apis.exceptions.JwtTokenException;
import com.crimson_code_blog_rest_apis.exceptions.ResourceNotFoundException;
import com.crimson_code_blog_rest_apis.repository.RoleRepository;
import com.crimson_code_blog_rest_apis.repository.TokenBlacklistRepository;
import com.crimson_code_blog_rest_apis.repository.UserRepository;
import com.crimson_code_blog_rest_apis.services.AuthService;
import com.crimson_code_blog_rest_apis.services.EmailService;
import com.crimson_code_blog_rest_apis.utils.JwtTokenType;
import com.crimson_code_blog_rest_apis.utils.JwtUtils;
import com.crimson_code_blog_rest_apis.utils.UserRoles;

@Service
public class AuthServiceImpl implements AuthService {

	private UserRepository userRepository;
	private ModelMapper modelMapper;
	private RoleRepository roleRepository;
	private JwtUtils jwtUtils;
	private EmailService emailService;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;
	private TokenBlacklistRepository tokenBlacklistRepository;
	
	@Autowired
	public AuthServiceImpl(UserRepository userRepository, ModelMapper modelMapper, RoleRepository roleRepository,
			JwtUtils jwtUtils, EmailService emailService, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, TokenBlacklistRepository tokenBlacklistRepository) {
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
		this.roleRepository = roleRepository;
		this.jwtUtils = jwtUtils;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.tokenBlacklistRepository = tokenBlacklistRepository;
	}

	@Override
	public UserResponseModel register(RegisterRequestModel registerRequest) {
		if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
			throw new CrimsonCodeGlobalException("This email already exists");
		}
		
		UserEntity newUser = modelMapper.map(registerRequest, UserEntity.class);
		
		newUser.setPublicId(UUID.randomUUID().toString());
		
		RoleEntity userRole = roleRepository.findByName(UserRoles.ROLE_USER.name())
				.orElseThrow(() -> new IllegalStateException(
						"Required role 'ROLE_USER' not found. Server misconfiguration detected.")
						);
		
		String emailVerificationToken = jwtUtils.generateEmailVerificationToken(newUser.getEmail());
		newUser.setEmailVerificationToken(emailVerificationToken);
		
		newUser.addRole(userRole);
		newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		newUser.setJoinedAt(OffsetDateTime.now(ZoneOffset.UTC));
		UserEntity savedUser = userRepository.save(newUser);
		
		UserResponseModel registerResponse = modelMapper.map(savedUser, UserResponseModel.class);
		
		emailService.sendVerificationEmail(savedUser.getEmail(), emailVerificationToken);
		
		return registerResponse;
	}


	@Override
	public LoginResponseModel login(LoginRequestModel loginRequest) {

		Authentication auth = 
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
		Authentication authenticatedToken = authenticationManager.authenticate(auth);
		
		SecurityContextHolder.getContext().setAuthentication(authenticatedToken);		
		
		UserPrincipal userPrincipal = (UserPrincipal) authenticatedToken.getPrincipal();
		
		Map<String, Object> userClaims = generateUserClaims(userPrincipal.getUserEntity());
		
		String accessToken = jwtUtils.generateAccessToken(loginRequest.getEmail(), userClaims);
		String refreshToken = jwtUtils.generateRefreshToken(loginRequest.getEmail());
		
		return new LoginResponseModel(accessToken, refreshToken);
	}


	@Override
	public void emailVerification(String token) {
		
		jwtUtils.validateJwtToken(token, JwtTokenType.EMAIL_VERIFICATION_TOKEN);
		
		UserEntity user = userRepository.findByEmailVerificationToken(token)
				.orElseThrow(() -> new JwtTokenException(JwtTokenType.EMAIL_VERIFICATION_TOKEN,
						"Invalid Email verification token "));
		
		user.setEmailVerificationToken(null);
		user.setIsEmailVerified(true);
		
		userRepository.save(user);
		
	}

	@Override
	public void emailVerificationRequest(EmailVerificationRequest verificationRequest) {
		String userEmail = verificationRequest.getEmail();

		UserEntity user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User does not exist with email: " + userEmail));
		
		if (user.getIsEmailVerified()) {
			throw new CrimsonCodeGlobalException("Your email has been already verified");
		}
		
		String emailVerificationToken = jwtUtils.generateEmailVerificationToken(userEmail);
		user.setEmailVerificationToken(emailVerificationToken);
		
		// Send a new email verification token to user's email address
		emailService.sendVerificationEmail(userEmail, emailVerificationToken);

		userRepository.save(user);
	}


	@Override
	public RefreshTokenResponseModel refreshAccessToken(String authorizationHeader) {

		if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new JwtTokenException(JwtTokenType.REFRESH_TOKEN, "Invalid Refresh token");
		}
		
		String refreshToken = authorizationHeader.substring(7);
		
		jwtUtils.validateJwtToken(refreshToken, JwtTokenType.REFRESH_TOKEN);
		
		String userEmail = jwtUtils.extractUsername(refreshToken);
		
		UserEntity user = userRepository.findByEmail(userEmail)
				.orElseThrow(() ->
				new JwtTokenException(JwtTokenType.REFRESH_TOKEN, "Invalid Refresh token"));
		
		Map<String, Object> userClaims = generateUserClaims(user);
		
		String newAccessToken = jwtUtils.generateAccessToken(userEmail, userClaims);
		 
		return new RefreshTokenResponseModel(newAccessToken);
	}
	
	private Map<String, Object> generateUserClaims(UserEntity user) {

		Map<String, Object> userClaims = new HashMap<>();
		
		List<String> userRoles = user.getRoles().stream()
				.map(role -> role.getName()).collect(Collectors.toList());
		userClaims.put("roles", userRoles);
		userClaims.put("userPublicId", user.getPublicId());
		
		return userClaims;
	}


	@Override
	public void logout(LogoutRequestModel logoutRequest, String authorizationHeader) {

		/*
		 * Access token has already checked and validated in the JwtAuthenticationFilter
		 * because the /logout end point is protected, so we don't need to re-verify it
		 */
		String accessToken = authorizationHeader.substring(7);
		
		LocalDateTime accessTokenExpiration = jwtUtils.extractExpirationDate(accessToken)
				.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

		LocalDateTime refreshTokenExpiration = jwtUtils.extractExpirationDate(logoutRequest.getRefreshToken())
				.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		
		TokenBlacklistEntity accessTokenBlacklistEntity = new TokenBlacklistEntity(
				accessToken, JwtTokenType.ACCESS_TOKEN.name(), accessTokenExpiration);
		
		TokenBlacklistEntity refreshTokenBlacklistEntity = new TokenBlacklistEntity(
				logoutRequest.getRefreshToken(), JwtTokenType.REFRESH_TOKEN.name(), refreshTokenExpiration);
		
		tokenBlacklistRepository.saveAll(new ArrayList<>(
				List.of(accessTokenBlacklistEntity, refreshTokenBlacklistEntity)));
		
		SecurityContextHolder.getContext().setAuthentication(null);
		
	}
	
}
