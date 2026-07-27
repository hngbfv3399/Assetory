package com.portfolio.assetory.product.dto.response;

import java.math.BigDecimal;

import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductStatus;

public record SellerProductCreateResponse(
	Long id,
	String name,
	BigDecimal price,
	ProductStatus status
) {
	public static SellerProductCreateResponse from(Product product) {
		return new SellerProductCreateResponse(
			product.getId(),
			product.getName(),
			product.getPrice(),
			product.getStatus()
		);
	}
}
