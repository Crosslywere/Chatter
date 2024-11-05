package com.chat.app.config;

import com.chat.app.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtility jwtUtility;

	private final UserService userService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
		final Cookie authCookie = request.getCookies() != null ?
				Arrays.stream(request.getCookies())
						.filter(AppUtility::isAuthCookie)
						.findFirst()
						.orElse(null) : null;
		if (authCookie == null) {
			filterChain.doFilter(request, response);
			return;
		}
		final String token = authCookie.getValue();
		if (!jwtUtility.isTokenValid(token)) {
			response.addCookie(AppUtility.deleteAuthCookie());
			filterChain.doFilter(request, response);
			return;
		}
		String username;
		try {
			username = jwtUtility.getSubject(token);
		} catch (UsernameNotFoundException e) {
			logger.error(e.getMessage());
			response.addCookie(AppUtility.deleteAuthCookie());
			filterChain.doFilter(request, response);
			return;
		}
		if (username != null && !username.isBlank() && SecurityContextHolder.getContext().getAuthentication() == null) {
			final UserDetails userDetails = userService.loadUserByUsername(username);
			var userToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);
			var context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(userToken);
			SecurityContextHolder.setContext(context);
		}
		filterChain.doFilter(request, response);
	}
}
