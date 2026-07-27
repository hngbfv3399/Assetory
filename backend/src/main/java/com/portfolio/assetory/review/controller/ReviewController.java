package com.portfolio.assetory.review.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.review.domain.ReviewSort;
import com.portfolio.assetory.review.dto.response.ReviewListResponse;
import com.portfolio.assetory.review.service.ReviewQueryService;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

	private final ReviewQueryService reviewQueryService;

	public ReviewController(ReviewQueryService reviewQueryService) {
		this.reviewQueryService = reviewQueryService;
	}

	@GetMapping
	public ApiResponse<ReviewListResponse> getReviews(
		@PathVariable Long productId,
		@RequestParam(defaultValue = "LATEST") ReviewSort sort,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ApiResponse.success(reviewQueryService.getPublicReviews(productId, sort, page, size));
	}
}
