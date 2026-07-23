package com.portfolio.assetory.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.member.dto.request.UpdateMyProfileRequest;
import com.portfolio.assetory.member.dto.response.MyProfileResponse;
import com.portfolio.assetory.member.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MyProfileResponse>> getMyProfile(@CurrentUserId Long userId) {
		return ResponseEntity.ok(ApiResponse.success(userService.getMyProfile(userId)));
	}

	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<MyProfileResponse>> updateMyProfile(
		@CurrentUserId Long userId,
		@Valid @RequestBody UpdateMyProfileRequest request
	) {
		return ResponseEntity.ok(ApiResponse.success(userService.updateMyProfile(userId, request)));
	}

	@DeleteMapping("/me")
	public ResponseEntity<ApiResponse<Void>> withdraw(@CurrentUserId Long userId) {
		userService.withdraw(userId);
		return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다.", null));
	}
}
