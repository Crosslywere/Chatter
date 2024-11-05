package com.chat.app.service;

import com.chat.app.config.JwtUtility;
import com.chat.app.controller.LoginController;
import com.chat.app.controller.RegisterController;
import com.chat.app.entity.Channel;
import com.chat.app.entity.User;
import com.chat.app.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final JwtUtility jwtUtility;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
	}

	public String login(@NonNull final LoginController.LoginDetails loginDetails) {
		var user = userRepository.findByUsername(loginDetails.username()).orElse(null);
		if (user == null) {
			return "";
		}
		if (!passwordEncoder.matches(loginDetails.password(), user.getPassword())) {
			return "";
		}
		return jwtUtility.generateToken(user);
	}

	public String register(@NonNull final RegisterController.RegistrationDetails registrationDetails) {
		if (userRepository.findByUsername(registrationDetails.getUsername()).isPresent()) {
			return "ERROR: Username is already in use";
		}
		if (registrationDetails.getPassword().length() < 8) {
			return "ERROR: Password is too short";
		}
		var user = userRepository.save(User.builder()
				.username(registrationDetails.getUsername())
				.password(passwordEncoder.encode(registrationDetails.getPassword()))
				.build());
		return jwtUtility.generateToken(user);
	}

	public List<Channel> getChannels() {
		if (SecurityContextHolder.getContext().getAuthentication() == null)
			return null;
		String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		var user = userRepository.findByUsername(username);
		if (user.isPresent()) {
			return user.get().getChannels();
		}
		return List.of();
	}

	public boolean isUsernameAvailable(String username) {
		if (username.isBlank())
			return false;
		return userRepository.findByUsername(username).isEmpty();
	}
}
