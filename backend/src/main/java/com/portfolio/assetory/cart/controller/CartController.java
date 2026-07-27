package com.portfolio.assetory.cart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.cart.dto.request.AddCartItemRequest;
import com.portfolio.assetory.cart.dto.response.CartItemAddResponse;
import com.portfolio.assetory.cart.dto.response.CartItemRemoveResponse;
import com.portfolio.assetory.cart.dto.response.CartResponse;
import com.portfolio.assetory.cart.service.CartService;
import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@PostMapping("/items")
	public ResponseEntity<ApiResponse<CartItemAddResponse>> addItem(
		@CurrentUserId Long userId,
		@Valid @RequestBody AddCartItemRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(cartService.addItem(userId, request)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<CartResponse>> getCart(@CurrentUserId Long userId) {
		return ResponseEntity.ok(ApiResponse.success(cartService.getCart(userId)));
	}

	@DeleteMapping("/items/{cartItemId}")
	public ResponseEntity<ApiResponse<CartItemRemoveResponse>> removeItem(
		@CurrentUserId Long userId,
		@PathVariable Long cartItemId
	) {
		return ResponseEntity.ok(ApiResponse.success(cartService.removeItem(userId, cartItemId)));
	}

	@DeleteMapping("/items")
	public ResponseEntity<ApiResponse<Void>> clearCart(@CurrentUserId Long userId) {
		cartService.clearCart(userId);
		return ResponseEntity.ok(ApiResponse.success("장바구니를 비웠습니다.", null));
	}
}
