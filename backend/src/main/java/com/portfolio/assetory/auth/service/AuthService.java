package com.portfolio.assetory.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.auth.domain.RefreshToken;
import com.portfolio.assetory.auth.dto.request.LoginRequest;
import com.portfolio.assetory.auth.dto.response.LoginResponse;
import com.portfolio.assetory.auth.dto.response.RefreshResponse;
import com.portfolio.assetory.auth.repository.RefreshTokenRepository;
import com.portfolio.assetory.auth.security.JwtTokenProvider;
import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.member.domain.User;
import com.portfolio.assetory.member.domain.UserStatus;
import com.portfolio.assetory.member.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AuthService {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final long refreshTokenExpiration;

	public AuthService(
		UserRepository userRepository,
		RefreshTokenRepository refreshTokenRepository,
		PasswordEncoder passwordEncoder,
		JwtTokenProvider jwtTokenProvider,
		@Value("${app.jwt.refresh-token-expiration}") long refreshTokenExpiration
	) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
		this.refreshTokenExpiration = refreshTokenExpiration;
	}

	@Transactional
	public LoginResult login(LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
			.orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BusinessException(ErrorCode.LOGIN_FAILED);
		}
		validateActiveUser(user);

		String rawRefreshToken = createRawRefreshToken();
		RefreshToken refreshToken = RefreshToken.issue(
			user,
			hash(rawRefreshToken),
			LocalDateTime.now().plusSeconds(refreshTokenExpiration)
		);
		refreshTokenRepository.save(refreshToken);

		LoginResponse response = new LoginResponse(
			jwtTokenProvider.createAccessToken(user),
			new LoginResponse.LoginUser(user.getId(), user.getEmail(), user.getNickname())
		);
		return new LoginResult(response, rawRefreshToken);
	}

	public RefreshResponse refresh(String rawRefreshToken) {
		RefreshToken refreshToken = findActiveRefreshToken(rawRefreshToken);
		validateActiveUser(refreshToken.getUser());
		return new RefreshResponse(jwtTokenProvider.createAccessToken(refreshToken.getUser()));
	}

	@Transactional
	public void logout(Long userId, String rawRefreshToken) {
		if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
			return;
		}

		RefreshToken refreshToken = findActiveRefreshToken(rawRefreshToken);
		if (!refreshToken.belongsTo(userId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
		refreshToken.revoke();
	}

	private RefreshToken findActiveRefreshToken(String rawRefreshToken) {
		if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		RefreshToken refreshToken = refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(hash(rawRefreshToken))
			.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));
		if (!refreshToken.isActiveAt(LocalDateTime.now())) {
			throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
		return refreshToken;
	}

	private void validateActiveUser(User user) {
		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new BusinessException(ErrorCode.USER_INACTIVE);
		}
	}

	private String createRawRefreshToken() {
		byte[] bytes = new byte[48];
		SECURE_RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private String hash(String value) {
		try {
			byte[] hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 algorithm is unavailable", exception);
		}
	}

	public record LoginResult(LoginResponse response, String rawRefreshToken) {
	}
}
