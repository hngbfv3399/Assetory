package com.portfolio.assetory.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.product.domain.ProductStatus;
import com.portfolio.assetory.product.repository.ProductRepository;
import com.portfolio.assetory.review.domain.Review;
import com.portfolio.assetory.review.domain.ReviewSort;
import com.portfolio.assetory.review.dto.response.ReviewListResponse;
import com.portfolio.assetory.review.repository.ReviewRepository;

@Service
@Transactional(readOnly = true)
public class ReviewQueryService {

	private static final int MAX_PAGE_SIZE = 100;

	private final ProductRepository productRepository;
	private final ReviewRepository reviewRepository;

	public ReviewQueryService(ProductRepository productRepository, ReviewRepository reviewRepository) {
		this.productRepository = productRepository;
		this.reviewRepository = reviewRepository;
	}

	public ReviewListResponse getPublicReviews(Long productId, ReviewSort sort, int page, int size) {
		validatePage(page, size);
		productRepository.findPublicProductById(productId, ProductStatus.ON_SALE)
			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

		Pageable pageable = PageRequest.of(page, size, resolveSort(sort));
		Page<Review> reviewPage = reviewRepository.findPublicReviewsByProductId(productId, pageable);
		return ReviewListResponse.from(reviewPage);
	}

	private void validatePage(int page, int size) {
		if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}
	}

	private Sort resolveSort(ReviewSort sort) {
		return switch (sort) {
			case LATEST -> Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
			case RATING_HIGH -> Sort.by(Sort.Order.desc("rating"), Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
			case RATING_LOW -> Sort.by(Sort.Order.asc("rating"), Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
		};
	}
}
