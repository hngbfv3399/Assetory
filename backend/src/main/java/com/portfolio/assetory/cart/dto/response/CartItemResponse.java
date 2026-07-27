package com.portfolio.assetory.cart.dto.response;

import java.math.BigDecimal;

import com.portfolio.assetory.cart.domain.CartItem;

public record CartItemResponse(
	Long cartItemId,
	Long productId,
	String name,
	BigDecimal price,
	String thumbnailUrl,
	String sellerNickname
) {
	public static CartItemResponse from(CartItem item, String thumbnailUrl) {
		return new CartItemResponse(
			item.getId(),
			item.getProduct().getId(),
			item.getProduct().getName(),
			item.getProduct().getPrice(),
			thumbnailUrl,
			item.getProduct().getSeller().getNickname()
		);
	}
}
