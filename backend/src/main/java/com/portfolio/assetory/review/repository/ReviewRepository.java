package com.portfolio.assetory.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.assetory.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	interface ProductReviewStatistics {
		Long getProductId();

		Long getReviewCount();

		Double getAverageRating();
	}

	@Query("""
		select review.product.id as productId,
		       count(review.id) as reviewCount,
		       avg(review.rating) as averageRating
		from Review review
		where review.product.id in :productIds
		  and review.deletedAt is null
		group by review.product.id
		""")
	List<ProductReviewStatistics> findStatisticsByProductIds(@Param("productIds") List<Long> productIds);

	@Query(
		value = """
			select review
			from Review review
			join fetch review.writer
			where review.product.id = :productId
			  and review.deletedAt is null
			""",
		countQuery = """
			select count(review)
			from Review review
			where review.product.id = :productId
			  and review.deletedAt is null
			"""
	)
	Page<Review> findPublicReviewsByProductId(@Param("productId") Long productId, Pageable pageable);
}
