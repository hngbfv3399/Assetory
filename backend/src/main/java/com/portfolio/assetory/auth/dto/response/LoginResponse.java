package com.portfolio.assetory.auth.dto.response;

public record LoginResponse(
	String accessToken,
	LoginUser user
) {
	public record LoginUser(Long id, String email, String nickname) {
	}
}
