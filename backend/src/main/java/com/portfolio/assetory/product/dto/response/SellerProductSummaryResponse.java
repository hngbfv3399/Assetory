package com.portfolio.assetory.product.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductStatus;

public record SellerProductSummaryResponse(
	Long id,
	String name,
	BigDecimal price,
	String thumbnailUrl,
	ProductStatus status,
	LocalDateTime createdAt
) {
	public static SellerProductSummaryResponse from(Product product, String thumbnailUrl) {
		return new SellerProductSummaryResponse(
			product.getId(),
			product.getName(),
			product.getPrice(),
			thumbnailUrl,
			product.getStatus(),
			product.getCreatedAt()
		);
	}
}
