package com.portfolio.assetory.review.dto.response;
import java.time.LocalDateTime; import java.util.List; import org.springframework.data.domain.Page; import com.portfolio.assetory.review.domain.Review;
public record MyReviewListResponse(List<ReviewItem> reviews, int page, int size, long totalElements, int totalPages) {
	public static MyReviewListResponse from(Page<Review> page, java.util.Map<Long,String> thumbnails) { return new MyReviewListResponse(page.getContent().stream().map(r -> new ReviewItem(r.getId(), new Product(r.getProduct().getId(), r.getProduct().getName(), thumbnails.get(r.getProduct().getId())), r.getRating(), r.getContent(), r.getCreatedAt(), r.getUpdatedAt())).toList(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()); }
	public record ReviewItem(Long id, Product product, int rating, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {}
	public record Product(Long id, String name, String thumbnailUrl) {}
}
