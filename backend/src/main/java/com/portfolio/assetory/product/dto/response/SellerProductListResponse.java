package com.portfolio.assetory.product.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.portfolio.assetory.product.domain.Product;

public record SellerProductListResponse(
	List<SellerProductSummaryResponse> products,
	int page,
	int totalPages
) {
	public static SellerProductListResponse from(Page<Product> productPage, List<SellerProductSummaryResponse> products) {
		return new SellerProductListResponse(products, productPage.getNumber(), productPage.getTotalPages());
	}
}
