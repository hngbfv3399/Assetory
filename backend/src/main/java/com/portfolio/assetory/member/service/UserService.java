package com.portfolio.assetory.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.auth.domain.RefreshToken;
import com.portfolio.assetory.auth.repository.RefreshTokenRepository;
import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.member.domain.User;
import com.portfolio.assetory.member.dto.request.SignupRequest;
import com.portfolio.assetory.member.dto.request.UpdateMyProfileRequest;
import com.portfolio.assetory.member.dto.response.MyProfileResponse;
import com.portfolio.assetory.member.dto.response.SignupResponse;
import com.portfolio.assetory.member.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(
		UserRepository userRepository,
		RefreshTokenRepository refreshTokenRepository,
		PasswordEncoder passwordEncoder
	) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public SignupResponse signup(SignupRequest request) {
		validateDuplicate(request);

		User user = User.register(
			request.email(),
			passwordEncoder.encode(request.password()),
			request.nickname()
		);
		User savedUser = userRepository.save(user);

		return new SignupResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname());
	}

	private void validateDuplicate(SignupRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
		if (userRepository.existsByNickname(request.nickname())) {
			throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}
	}

	public MyProfileResponse getMyProfile(Long userId) {
		return toMyProfileResponse(findActiveUser(userId));
	}

	@Transactional
	public MyProfileResponse updateMyProfile(Long userId, UpdateMyProfileRequest request) {
		User user = findActiveUser(userId);
		if (request.nickname() != null && !request.nickname().equals(user.getNickname())
			&& userRepository.existsByNickname(request.nickname())) {
			throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}
		user.updateProfile(request.nickname(), request.profileImageUrl());
		return toMyProfileResponse(user);
	}

	@Transactional
	public void withdraw(Long userId) {
		User user = findActiveUser(userId);
		for (RefreshToken refreshToken : refreshTokenRepository.findAllByUser_IdAndRevokedAtIsNull(userId)) {
			refreshToken.revoke();
		}
		user.withdraw();
	}

	private User findActiveUser(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		if (user.getStatus() != com.portfolio.assetory.member.domain.UserStatus.ACTIVE) {
			throw new BusinessException(ErrorCode.USER_INACTIVE);
		}
		return user;
	}

	private MyProfileResponse toMyProfileResponse(User user) {
		return new MyProfileResponse(
			user.getId(),
			user.getEmail(),
			user.getNickname(),
			user.getProfileImageUrl(),
			user.getCreatedAt()
		);
	}
}
