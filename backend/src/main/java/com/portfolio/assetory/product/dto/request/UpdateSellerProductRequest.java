package com.portfolio.assetory.product.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;

public record UpdateSellerProductRequest(
	Long categoryId,
	String name,
	String summary,
	String description,
	@DecimalMin(value = "0", inclusive = false) BigDecimal price
) {
}
