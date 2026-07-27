package com.portfolio.assetory.product.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.portfolio.assetory.product.domain.Product;

public record ProductListResponse(
	List<ProductSummaryResponse> products,
	int page,
	int size,
	long totalElements,
	int totalPages
) {
	public static ProductListResponse from(Page<Product> productPage, List<ProductSummaryResponse> products) {
		return new ProductListResponse(
			products,
			productPage.getNumber(),
			productPage.getSize(),
			productPage.getTotalElements(),
			productPage.getTotalPages()
		);
	}
}
