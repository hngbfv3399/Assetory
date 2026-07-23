package com.portfolio.assetory.member.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String nickname;

	private String phone;

	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserStatus status;

	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(
		name = "user_roles",
		joinColumns = @JoinColumn(name = "user_id"),
		uniqueConstraints = @UniqueConstraint(
			name = "uk_user_roles_user_id_role",
			columnNames = {"user_id", "role"}
		)
	)
	@Column(name = "role", nullable = false, length = 20)
	private Set<UserRole> roles = new HashSet<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	protected User() {
	}

	public static User register(String email, String encodedPassword, String nickname) {
		User user = new User();
		user.email = email;
		user.password = encodedPassword;
		user.nickname = nickname;
		return user;
	}

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (status == null) {
			status = UserStatus.ACTIVE;
		}
		if (roles.isEmpty()) {
			roles.add(UserRole.BUYER);
			roles.add(UserRole.SELLER);
		}
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void addRole(UserRole role) {
		roles.add(role);
	}

	public void updateProfile(String nickname, String profileImageUrl) {
		if (nickname != null) {
			this.nickname = nickname;
		}
		if (profileImageUrl != null) {
			this.profileImageUrl = profileImageUrl;
		}
	}

	public void withdraw() {
		status = UserStatus.WITHDRAWN;
		deletedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPassword() {
		return password;
	}

	public UserStatus getStatus() {
		return status;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
