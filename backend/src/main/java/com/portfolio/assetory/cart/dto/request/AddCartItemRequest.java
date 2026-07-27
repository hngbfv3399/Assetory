package com.portfolio.assetory.cart.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
	@NotNull Long productId
) {
}
