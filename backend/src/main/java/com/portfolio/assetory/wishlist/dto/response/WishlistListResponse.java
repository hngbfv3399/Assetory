package com.portfolio.assetory.wishlist.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.portfolio.assetory.wishlist.domain.Wishlist;

public record WishlistListResponse(
	List<WishlistProductResponse> products,
	int page,
	int totalPages
) {
	public static WishlistListResponse from(Page<Wishlist> wishlistPage, List<WishlistProductResponse> products) {
		return new WishlistListResponse(products, wishlistPage.getNumber(), wishlistPage.getTotalPages());
	}
}
