package com.crimson_code_blog_rest_apis.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.crimson_code_blog_rest_apis.utils.JwtUtils;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private UserDetailsService userDetailsService;
	private JwtUtils jwtUtils;
	private HandlerExceptionResolver exceptionResolver;
	
	@Autowired
	public SecurityConfig(UserDetailsService userDetailsService, JwtUtils jwtUtils,
			@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
		this.userDetailsService = userDetailsService;
		this.jwtUtils = jwtUtils;
		this.exceptionResolver = exceptionResolver;
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.cors(Customizer.withDefaults()) // By default uses a Bean by the name of corsConfigurationSource
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> {
					authorize.requestMatchers("/api/auth/logout").authenticated()
					.requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
					.requestMatchers("/api/users/*/follow/**").authenticated()
					
					/*
					 * Permit access to get user by public id when using the request /api/users/{userId}
					 * as well as those with additional path segments 
					 * as /* means there must be one segment after /api/users/ which in our case the {userId}
					 * and /** means Zero or more segments which means 
					 * /api/users/{userId}/posts and /api/users/{userId} will match without any issue.
					 */
					.requestMatchers(HttpMethod.GET, "/api/users/*/**").permitAll()
					.requestMatchers("/api/users/password-reset/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/images/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
					.requestMatchers("/swagger-ui/**").permitAll()
					.requestMatchers("/v3/api-docs/**").permitAll()
					
					.requestMatchers("/api/auth/**").permitAll()
					
					.requestMatchers("/api/users/**").authenticated()
					
					/*
					 * Make the default error handling endpoint accessible for everyone
					 * to make the error shown in the response body when an error occurs
					 */
					.requestMatchers("/error").permitAll()
					.anyRequest().authenticated();
				})
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				
				/* 
				 * This handles the unauthorized exceptions manually to return 401
				 * because spring by default return 403 when the user is unauthorized
				 * instead of 401 when using custom login functionality like JWT in our case
				 */

				.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, exception) -> {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
				}))
				.build();
	}
	
	private JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtUtils, exceptionResolver, userDetailsService);
	}

	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
		daoAuthProvider.setPasswordEncoder(passwordEncoder());
		daoAuthProvider.setUserDetailsService(userDetailsService);
		
		return daoAuthProvider;
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration AuthConfig) throws Exception {
		return AuthConfig.getAuthenticationManager();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		
		configuration.setAllowedOriginPatterns(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("content-type", "Authorization")); 
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
