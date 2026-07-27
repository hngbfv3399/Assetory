package com.portfolio.assetory.wishlist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.wishlist.dto.response.WishlistListResponse;
import com.portfolio.assetory.wishlist.dto.response.WishlistStatusResponse;
import com.portfolio.assetory.wishlist.service.WishlistService;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {

	private final WishlistService wishlistService;

	public WishlistController(WishlistService wishlistService) {
		this.wishlistService = wishlistService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<WishlistListResponse>> getWishlists(
		@CurrentUserId Long userId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	) {
		return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlists(userId, page, size)));
	}

	@PostMapping("/products/{productId}")
	public ResponseEntity<ApiResponse<WishlistStatusResponse>> addWishlist(
		@CurrentUserId Long userId,
		@PathVariable Long productId
	) {
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success(wishlistService.addWishlist(userId, productId)));
	}

	@DeleteMapping("/products/{productId}")
	public ResponseEntity<ApiResponse<WishlistStatusResponse>> removeWishlist(
		@CurrentUserId Long userId,
		@PathVariable Long productId
	) {
		return ResponseEntity.ok(ApiResponse.success(wishlistService.removeWishlist(userId, productId)));
	}
}
