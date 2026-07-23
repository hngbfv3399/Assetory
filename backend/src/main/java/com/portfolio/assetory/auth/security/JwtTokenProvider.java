package com.portfolio.assetory.auth.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.member.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final long accessTokenExpiration;

	public JwtTokenProvider(
		@Value("${app.jwt.secret}") String secret,
		@Value("${app.jwt.access-token-expiration}") long accessTokenExpiration
	) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpiration = accessTokenExpiration;
	}

	public String createAccessToken(User user) {
		Instant now = Instant.now();
		return Jwts.builder()
			.subject(user.getId().toString())
			.claim("email", user.getEmail())
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusSeconds(accessTokenExpiration)))
			.signWith(secretKey)
			.compact();
	}

	public Long getUserId(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
			return Long.valueOf(claims.getSubject());
		} catch (JwtException | IllegalArgumentException exception) {
			throw new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN);
		}
	}
}
