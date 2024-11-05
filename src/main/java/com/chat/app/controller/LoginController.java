package com.chat.app.controller;

import com.chat.app.config.AppUtility;
import com.chat.app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

	private final UserService userService;

	public record LoginDetails(String username, String password) {
		public LoginDetails() {
			this("", "");
		}
	}

	@GetMapping
	public String loginPage(Model model, HttpServletRequest request) {
		Arrays.stream(request.getCookies() == null ? Collections.emptyList().toArray() : request.getCookies()).filter(AppUtility::isAuthCookie).findFirst().ifPresent(cookie -> System.out.println("Cookie is present " + ((Cookie) cookie).getValue()));
		model.addAttribute("loginDetails", new LoginDetails());
		return "login";
	}

	@PostMapping
	public String loginRequest(@NonNull final LoginDetails loginDetails, RedirectAttributes attributes, HttpServletResponse response) {
		response.addCookie(AppUtility.deleteAuthCookie());
		final String token = userService.login(loginDetails);
		if (token.isBlank()) {
			attributes.addFlashAttribute("error", "Invalid login credentials");
			return "redirect:/login";
		}
		response.addCookie(AppUtility.createAuthCookie(token));
		return "redirect:/channels";
	}
}
