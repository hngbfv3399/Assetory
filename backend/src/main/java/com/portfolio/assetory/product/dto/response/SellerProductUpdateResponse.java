package com.portfolio.assetory.product.dto.response;

import java.math.BigDecimal;

import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductStatus;

public record SellerProductUpdateResponse(Long id, String name, BigDecimal price, Long categoryId, ProductStatus status) {
	public static SellerProductUpdateResponse from(Product product) {
		return new SellerProductUpdateResponse(product.getId(), product.getName(), product.getPrice(), product.getCategory().getId(), product.getStatus());
	}
}
