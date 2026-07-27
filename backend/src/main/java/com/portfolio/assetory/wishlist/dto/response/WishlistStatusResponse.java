package com.portfolio.assetory.wishlist.dto.response;

public record WishlistStatusResponse(
	Long productId,
	boolean wished
) {
	public static WishlistStatusResponse wished(Long productId) {
		return new WishlistStatusResponse(productId, true);
	}

	public static WishlistStatusResponse unwished(Long productId) {
		return new WishlistStatusResponse(productId, false);
	}
}
