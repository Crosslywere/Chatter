package com.chat.app.config;

import com.chat.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;

	private final MyLogoutFilter logoutFilter;

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(
						authManager -> authManager
								.requestMatchers("/login", "/register", "/logout", "/css/**.css", "/js/**.js", "/ws/**", "/topic/**")
								.permitAll()
								.anyRequest()
								.authenticated()
				)
				.formLogin(
						loginConfig -> loginConfig
								.loginPage("/login")
				)
				.logout(
						logoutConfig -> logoutConfig
								.logoutUrl("/logout")
				)
				.sessionManagement(
						sessionManager -> sessionManager
								.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
//				.addFilterBefore(logoutFilter, LogoutFilter.class)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.authenticationProvider(authenticationProvider())
				.build();
	}

	private AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}
}
