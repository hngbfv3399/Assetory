package com.portfolio.assetory.wishlist.dto.response;

import java.math.BigDecimal;

import com.portfolio.assetory.product.domain.Product;

public record WishlistProductResponse(
	Long id,
	String name,
	BigDecimal price,
	String thumbnailUrl,
	String sellerNickname
) {
	public static WishlistProductResponse from(Product product, String thumbnailUrl) {
		return new WishlistProductResponse(
			product.getId(),
			product.getName(),
			product.getPrice(),
			thumbnailUrl,
			product.getSeller().getNickname()
		);
	}
}
