package com.portfolio.assetory.product.service;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductImage;
import com.portfolio.assetory.product.domain.ProductImageType;
import com.portfolio.assetory.product.domain.ProductSort;
import com.portfolio.assetory.product.domain.ProductStatus;
import com.portfolio.assetory.product.dto.response.ProductListResponse;
import com.portfolio.assetory.product.dto.response.ProductDetailResponse;
import com.portfolio.assetory.product.dto.response.ProductSummaryResponse;
import com.portfolio.assetory.product.repository.ProductImageRepository;
import com.portfolio.assetory.product.repository.ProductRepository;
import com.portfolio.assetory.review.repository.ReviewRepository;
import com.portfolio.assetory.review.repository.ReviewRepository.ProductReviewStatistics;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional(readOnly = true)
public class ProductQueryService {

	private static final int MAX_PAGE_SIZE = 100;

	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;
	private final ReviewRepository reviewRepository;

	public ProductQueryService(
		ProductRepository productRepository,
		ProductImageRepository productImageRepository,
		ReviewRepository reviewRepository
	) {
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
		this.reviewRepository = reviewRepository;
	}

	public ProductListResponse getPublicProducts(
		Long categoryId,
		String keyword,
		ProductSort sort,
		int page,
		int size
	) {
		validatePage(page, size);

		Page<Product> productPage = findPublicProducts(categoryId, keyword, sort, page, size);

		Map<Long, String> thumbnailUrls = getThumbnailUrls(productPage.getContent());
		Map<Long, ProductReviewStatistics> reviewStatistics = getReviewStatistics(productPage.getContent());
		List<ProductSummaryResponse> products = productPage.getContent()
			.stream()
			.map(product -> ProductSummaryResponse.from(
				product,
				thumbnailUrls.get(product.getId()),
				getAverageRating(reviewStatistics.get(product.getId())),
				getReviewCount(reviewStatistics.get(product.getId()))
			))
			.toList();

		return ProductListResponse.from(productPage, products);
	}

	public ProductDetailResponse getPublicProduct(Long productId) {
		Product product = productRepository.findPublicProductById(productId, ProductStatus.ON_SALE)
			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

		List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAscIdAsc(productId);
		ProductReviewStatistics reviewStatistics = getReviewStatistics(List.of(product)).get(productId);
		return ProductDetailResponse.from(
			product,
			images,
			getAverageRating(reviewStatistics),
			getReviewCount(reviewStatistics)
		);
	}

	private Page<Product> findPublicProducts(
		Long categoryId,
		String keyword,
		ProductSort sort,
		int page,
		int size
	) {
		if (sort == ProductSort.POPULAR) {
			return productRepository.findPublicProductsOrderByReviewCount(
				categoryId,
				normalizeKeyword(keyword),
				ProductStatus.ON_SALE,
				PageRequest.of(page, size)
			);
		}

		return productRepository.findAll(
			publicProductSpecification(categoryId, keyword),
			PageRequest.of(page, size, resolveSort(sort))
		);
	}

	private void validatePage(int page, int size) {
		if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}
	}

	private Specification<Product> publicProductSpecification(Long categoryId, String keyword) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get("status"), ProductStatus.ON_SALE));
			predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

			if (categoryId != null) {
				predicates.add(criteriaBuilder.or(
					criteriaBuilder.equal(root.get("category").get("id"), categoryId),
					criteriaBuilder.equal(root.get("category").get("parent").get("id"), categoryId)
				));
			}

			if (keyword != null && !keyword.isBlank()) {
				String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
				predicates.add(criteriaBuilder.or(
					criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
					criteriaBuilder.like(criteriaBuilder.lower(root.get("summary")), pattern)
				));
			}

			return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
		};
	}

	private Sort resolveSort(ProductSort sort) {
		return switch (sort) {
			case LATEST -> Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
			case PRICE_LOW -> Sort.by(Sort.Order.asc("price"), Sort.Order.desc("id"));
			case PRICE_HIGH -> Sort.by(Sort.Order.desc("price"), Sort.Order.desc("id"));
			case POPULAR -> throw new IllegalArgumentException("POPULAR 정렬은 전용 집계 쿼리로 처리해야 합니다.");
		};
	}

	private Map<Long, String> getThumbnailUrls(List<Product> products) {
		List<Long> productIds = products.stream().map(Product::getId).toList();
		if (productIds.isEmpty()) {
			return Map.of();
		}

		return productImageRepository
			.findThumbnailsByProductIds(productIds, ProductImageType.THUMBNAIL)
			.stream()
			.collect(Collectors.toMap(
				ProductImage::getProductId,
				ProductImage::getImageUrl,
				(first, ignored) -> first
			));
	}

	private Map<Long, ProductReviewStatistics> getReviewStatistics(List<Product> products) {
		List<Long> productIds = products.stream().map(Product::getId).toList();
		if (productIds.isEmpty()) {
			return Map.of();
		}

		return reviewRepository.findStatisticsByProductIds(productIds)
			.stream()
			.collect(Collectors.toMap(ProductReviewStatistics::getProductId, statistics -> statistics));
	}

	private String normalizeKeyword(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}
		return keyword.trim();
	}

	private BigDecimal getAverageRating(ProductReviewStatistics reviewStatistics) {
		if (reviewStatistics == null || reviewStatistics.getAverageRating() == null) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(reviewStatistics.getAverageRating());
	}

	private long getReviewCount(ProductReviewStatistics reviewStatistics) {
		return reviewStatistics == null ? 0 : reviewStatistics.getReviewCount();
	}
}
