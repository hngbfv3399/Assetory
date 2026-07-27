package com.portfolio.assetory.product.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSellerProductRequest(
	@NotNull Long categoryId,
	@NotBlank String name,
	@NotBlank String summary,
	@NotBlank String description,
	@NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal price
) {
}
