package com.chat.app.config;

import jakarta.servlet.http.Cookie;
import org.springframework.lang.NonNull;

public class AppUtility {

	private final static String TOKEN_NAME = "JWT_AUTH";

	public static Cookie createAuthCookie(@NonNull final String token) {
		Cookie cookie = new Cookie(TOKEN_NAME, token);
		cookie.setMaxAge(JwtUtility.EXPIRATION_TIME);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}

	public static boolean isAuthCookie(@NonNull final Object cookie) {
		if (cookie instanceof Cookie)
			return ((Cookie) cookie).getName().equals(TOKEN_NAME);
		return false;
	}

	public static Cookie deleteAuthCookie() {
		Cookie cookie = new Cookie(TOKEN_NAME, "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
}
