package com.chat.app.controller;

import com.chat.app.config.AppUtility;
import com.chat.app.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegisterController {

	private final UserService userService;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegistrationDetails {
		private String username;
		private String password;
	}

	@Data
	@AllArgsConstructor
	public static class UsernameCheckResponse {
		private String message;
		private boolean available;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UsernameCheckRequest {
		private String username;
	}

	@GetMapping("/register")
	public String registrationPage(Model model) {
		model.addAttribute("registrationDetails", new RegistrationDetails());
		return "register";
	}

	@PostMapping("/register")
	public String register(final RegistrationDetails registrationDetails, RedirectAttributes attributes, HttpServletResponse response) {
		final String token = userService.register(registrationDetails);
		if (token.startsWith("ERROR: ")) {
			attributes.addFlashAttribute("error", token.substring(6));
			return "redirect:/register";
		}
		response.addCookie(AppUtility.createAuthCookie(token));
		return "redirect:/channels";
	}

	@MessageMapping("/check-username")
	@SendTo("/topic/username-availability")
	public UsernameCheckResponse checkUsername(UsernameCheckRequest request) {
		boolean isAvailable = userService.isUsernameAvailable(request.getUsername());
		return new UsernameCheckResponse(isAvailable ? "That name is available" : request.getUsername().isBlank() ? "Username cannot be blank" : "That name has already been taken", isAvailable);
	}
}
