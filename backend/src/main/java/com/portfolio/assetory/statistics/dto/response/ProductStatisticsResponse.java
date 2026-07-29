package com.portfolio.assetory.statistics.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.portfolio.assetory.product.domain.ProductStatus;

public record ProductStatisticsResponse(
	List<Product> products,
	int page,
	int size,
	long totalElements,
	int totalPages
) {
	public record Product(
		Long productId,
		String productName,
		String thumbnailUrl,
		ProductStatus status,
		long salesCount,
		BigDecimal totalSales,
		long refundCount,
		BigDecimal refundAmount,
		BigDecimal netSales,
		BigDecimal refundRate
	) {}
}
