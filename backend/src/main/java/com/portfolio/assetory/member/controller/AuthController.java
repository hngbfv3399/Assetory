package com.portfolio.assetory.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.auth.dto.request.LoginRequest;
import com.portfolio.assetory.auth.dto.response.LoginResponse;
import com.portfolio.assetory.auth.dto.response.RefreshResponse;
import com.portfolio.assetory.auth.service.AuthService;
import com.portfolio.assetory.auth.service.AuthService.LoginResult;
import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.member.dto.request.SignupRequest;
import com.portfolio.assetory.member.dto.response.SignupResponse;
import com.portfolio.assetory.member.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService userService;
	private final AuthService authService;

	public AuthController(
		UserService userService,
		AuthService authService
	) {
		this.userService = userService;
		this.authService = authService;
	}

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
		SignupResponse response = userService.signup(request);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success(response));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
		LoginResult result = authService.login(request);
		return ResponseEntity.ok()
			.header("Set-Cookie", createRefreshTokenCookie(result.rawRefreshToken()).toString())
			.body(ApiResponse.success(result.response()));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<RefreshResponse>> refresh(
		@CookieValue(value = "refreshToken", required = false) String refreshToken
	) {
		return ResponseEntity.ok(ApiResponse.success(authService.refresh(refreshToken)));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@CookieValue(value = "refreshToken", required = false) String refreshToken,
		@CurrentUserId Long userId
	) {
		authService.logout(userId, refreshToken);
		return ResponseEntity.ok()
			.header("Set-Cookie", deleteRefreshTokenCookie().toString())
			.body(ApiResponse.success("로그아웃되었습니다.", null));
	}

	private ResponseCookie createRefreshTokenCookie(String refreshToken) {
		return ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(true)
			.secure(false)
			.sameSite("Lax")
			.path("/api/auth")
			.maxAge(1_209_600)
			.build();
	}

	private ResponseCookie deleteRefreshTokenCookie() {
		return ResponseCookie.from("refreshToken", "")
			.httpOnly(true)
			.secure(false)
			.sameSite("Lax")
			.path("/api/auth")
			.maxAge(0)
			.build();
	}
}
