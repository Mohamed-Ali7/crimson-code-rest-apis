package com.crimson_code_blog_rest_apis.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.crimson_code_blog_rest_apis.utils.JwtTokenType;
import com.crimson_code_blog_rest_apis.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private JwtUtils jwtUtils;
	private HandlerExceptionResolver exceptionResolver;
	private UserDetailsService userDetailsService;
	//private Map<String, List<HttpMethod>> skipFilterUrls;
	
	public JwtAuthenticationFilter(JwtUtils jwtUtils, HandlerExceptionResolver exceptionResolver,
			UserDetailsService userDetailsService) {
		this.jwtUtils = jwtUtils;
		this.exceptionResolver = exceptionResolver;
		this.userDetailsService = userDetailsService;
		
		//skipFilterUrls = new HashMap<>();
		
		//skipFilterUrls.put("/api/auth/**", List.of(HttpMethod.GET, HttpMethod.POST));
		//skipFilterUrls.put("/api/users/*/**", List.of(HttpMethod.GET));
		//skipFilterUrls.put("/api/users/password-reset/**", List.of(HttpMethod.GET, HttpMethod.POST));
		//skipFilterUrls.put("/images/**", List.of(HttpMethod.GET));
		//skipFilterUrls.put("/api/categories/**", List.of(HttpMethod.GET));
		//skipFilterUrls.put("/api/posts/**", List.of(HttpMethod.GET));
		//skipFilterUrls.put("/api/tags/**", List.of(HttpMethod.GET));
	}
	/*
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String url = "";
		List<HttpMethod> httpMethods;

		if (request.getServletPath().equals("/api/auth/logout") || 
				request.getServletPath().equals("/api/users/me")) {
			return false;
		}
		
		for (Map.Entry<String, List<HttpMethod>> entry : skipFilterUrls.entrySet()) {
			
			url = entry.getKey();
			httpMethods = entry.getValue();
			
			for (HttpMethod method : httpMethods) {
				if (new AntPathRequestMatcher(url, method.name()).matches(request)) {
					return true;
				}
			}
		}
		
		return false;
	}
	*/

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (request.getServletPath().equals("/api/auth/refresh") || 
				request.getServletPath().equals("/api/auth/login") || 
				request.getServletPath().equals("/api/auth/register")) {
			
			filterChain.doFilter(request, response);
			return;
		}
		
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			doFilter(request, response, filterChain);
			return;
		}
		
		String token = authHeader.substring(7);
		
		try {
			jwtUtils.validateJwtToken(token, JwtTokenType.ACCESS_TOKEN);
		} catch (Exception ex) {
			
			/*
			 * Redirect the exception to be handled by the Custom Exception handler
			 * which in our case GlobalExceptionHandler.class
			 */
			exceptionResolver.resolveException(request, response, null, ex);
			
			// return to prevent the code from going beyond this point
			return;
		}
		
		String username = jwtUtils.extractUsername(token);
		
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && 
				!jwtUtils.tokenIsBlacklisted(token)) {

			UserPrincipal user = (UserPrincipal) userDetailsService.loadUserByUsername(username);
			
			Authentication authentication = 
					new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
		doFilter(request, response, filterChain);

	}

}
