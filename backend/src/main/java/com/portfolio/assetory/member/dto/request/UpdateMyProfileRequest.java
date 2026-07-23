package com.portfolio.assetory.member.dto.request;

import jakarta.validation.constraints.Pattern;

public record UpdateMyProfileRequest(
	@Pattern(regexp = ".*\\S.*", message = "닉네임은 공백만으로 구성할 수 없습니다.") String nickname,
	String profileImageUrl
) {
}
