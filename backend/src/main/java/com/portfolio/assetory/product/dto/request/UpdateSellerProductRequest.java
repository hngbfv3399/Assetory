package com.portfolio.assetory.product.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.portfolio.assetory.product.domain.ProductSaleType;

import jakarta.validation.constraints.DecimalMin;

public record UpdateSellerProductRequest(
	Long categoryId,
	String name,
	String summary,
	String description,
	@DecimalMin(value = "0", inclusive = false) BigDecimal price,
	ProductSaleType saleType,
	@DecimalMin(value = "0", inclusive = false) BigDecimal minimumPrice,
	LocalDateTime releaseAt
) {
}
