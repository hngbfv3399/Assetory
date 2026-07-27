package com.portfolio.assetory.review.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.portfolio.assetory.review.domain.Review;

public record ReviewListResponse(
	List<ReviewSummaryResponse> reviews,
	int page,
	int size,
	long totalElements,
	int totalPages
) {
	public static ReviewListResponse from(Page<Review> reviewPage) {
		return new ReviewListResponse(
			reviewPage.getContent().stream().map(ReviewSummaryResponse::from).toList(),
			reviewPage.getNumber(),
			reviewPage.getSize(),
			reviewPage.getTotalElements(),
			reviewPage.getTotalPages()
		);
	}
}
