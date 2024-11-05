package com.chat.app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtility {

	private static final String SECRET_KEY = "aehbaehaohgro1034yh10hoaiheh103rh410hoalaoeh08h4lan";

	private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

	private static final String ISSUER = "Chat-App";

	// In seconds for 1 day
	public static final int EXPIRATION_TIME = 60 * 60 * 24;

	public String generateToken(@NonNull final UserDetails userDetails) {
		long now = System.currentTimeMillis();
		return Jwts.builder()
				.subject(userDetails.getUsername())
				.issuer(ISSUER)
				.issuedAt(new Date(now))
				.expiration(new Date((1000 * EXPIRATION_TIME) + now))
				.signWith(SIGNING_KEY)
				.compact();
	}

	private Claims extractClaims(@NonNull final String token) {
		try {
			return Jwts.parser()
					.verifyWith(SIGNING_KEY)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public <T> T getClaimsFromToken(@NonNull final String token, @NonNull Function<Claims, T> resolver) {
		Claims claims = extractClaims(token);
		return resolver.apply(claims);
	}

	public boolean isTokenValid(final String token) {
		return isTokenExpired(token) && isTokenIssuerValid(token);
	}

	public boolean isTokenExpired(final String token) {
		final Date expiration = getClaimsFromToken(token, Claims::getExpiration);
		return expiration.after(new Date(System.currentTimeMillis()));
	}

	public boolean isTokenIssuerValid(final String token) {
		final String issuer = getClaimsFromToken(token, Claims::getIssuer);
		return issuer.equals(ISSUER);
	}

	public String getSubject(final String token) {
		return getClaimsFromToken(token, Claims::getSubject);
	}
}
