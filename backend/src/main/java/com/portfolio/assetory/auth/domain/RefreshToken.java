package com.portfolio.assetory.auth.domain;

import java.time.LocalDateTime;

import com.portfolio.assetory.member.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "token_hash", nullable = false, unique = true)
	private String tokenHash;

	private String deviceInfo;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	private LocalDateTime revokedAt;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	protected RefreshToken() {
	}

	public static RefreshToken issue(User user, String tokenHash, LocalDateTime expiresAt) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.user = user;
		refreshToken.tokenHash = tokenHash;
		refreshToken.expiresAt = expiresAt;
		refreshToken.createdAt = LocalDateTime.now();
		return refreshToken;
	}

	public boolean isActiveAt(LocalDateTime now) {
		return revokedAt == null && expiresAt.isAfter(now);
	}

	public boolean belongsTo(Long userId) {
		return user.getId().equals(userId);
	}

	public User getUser() {
		return user;
	}

	public void revoke() {
		if (revokedAt == null) {
			revokedAt = LocalDateTime.now();
		}
	}
}
