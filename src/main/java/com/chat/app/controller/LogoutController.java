package com.chat.app.controller;

import com.chat.app.config.AppUtility;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logout")
public class LogoutController {

	@GetMapping
	@ExceptionHandler(UsernameNotFoundException.class)
	public String logout(HttpServletResponse response) {
		response.addCookie(AppUtility.deleteAuthCookie());
		return "redirect:/login";
	}
}
