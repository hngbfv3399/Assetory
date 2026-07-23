package com.portfolio.assetory.member.dto.response;

import java.time.LocalDateTime;

public record MyProfileResponse(
	Long id,
	String email,
	String nickname,
	String profileImageUrl,
	LocalDateTime createdAt
) {
}
