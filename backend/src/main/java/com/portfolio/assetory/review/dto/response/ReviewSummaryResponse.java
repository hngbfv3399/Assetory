package com.portfolio.assetory.review.dto.response;

import java.time.LocalDateTime;

import com.portfolio.assetory.review.domain.Review;

public record ReviewSummaryResponse(
	Long id,
	String writerNickname,
	int rating,
	String content,
	LocalDateTime createdAt
) {
	public static ReviewSummaryResponse from(Review review) {
		return new ReviewSummaryResponse(
			review.getId(),
			review.getWriter().getNickname(),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt()
		);
	}
}
