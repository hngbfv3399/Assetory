package com.portfolio.assetory.cart.dto.response;

public record CartItemRemoveResponse(
	Long productId,
	boolean removed
) {
	public static CartItemRemoveResponse removed(Long productId) {
		return new CartItemRemoveResponse(productId, true);
	}
}
