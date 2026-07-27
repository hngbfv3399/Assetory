package com.portfolio.assetory.product.dto.response;

import java.math.BigDecimal;

import com.portfolio.assetory.product.domain.Product;

public record ProductSummaryResponse(
	Long id,
	String name,
	String summary,
	BigDecimal price,
	String thumbnailUrl,
	String sellerNickname,
	BigDecimal averageRating,
	long reviewCount
) {
	public static ProductSummaryResponse from(
		Product product,
		String thumbnailUrl,
		BigDecimal averageRating,
		long reviewCount
	) {
		return new ProductSummaryResponse(
			product.getId(),
			product.getName(),
			product.getSummary(),
			product.getPrice(),
			thumbnailUrl,
			product.getSeller().getNickname(),
			averageRating,
			reviewCount
		);
	}
}
