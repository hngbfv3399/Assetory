package com.portfolio.assetory.cart.dto.response;

public record CartItemAddResponse(
	Long productId,
	boolean added
) {
	public static CartItemAddResponse added(Long productId) {
		return new CartItemAddResponse(productId, true);
	}
}
