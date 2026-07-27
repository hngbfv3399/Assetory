package com.portfolio.assetory.review.dto.response;
import java.time.LocalDateTime; import com.portfolio.assetory.review.domain.Review;
public record MyReviewResponse(Long id, Long productId, String productName, String writerNickname, int rating, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
	public static MyReviewResponse from(Review review) { return new MyReviewResponse(review.getId(), review.getProduct().getId(), review.getProduct().getName(), review.getWriter().getNickname(), review.getRating(), review.getContent(), review.getCreatedAt(), review.getUpdatedAt()); }
}
