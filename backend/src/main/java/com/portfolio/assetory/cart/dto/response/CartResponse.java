package com.portfolio.assetory.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
	List<CartItemResponse> items,
	BigDecimal totalPrice,
	int itemCount
) {
	public static CartResponse empty() {
		return new CartResponse(List.of(), BigDecimal.ZERO, 0);
	}
}
