package com.portfolio.assetory.sellerorder.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.order.domain.OrderStatus;
import com.portfolio.assetory.sellerorder.dto.response.SellerOrderCountsResponse;
import com.portfolio.assetory.sellerorder.dto.response.SellerOrderDetailResponse;
import com.portfolio.assetory.sellerorder.dto.response.SellerOrderListResponse;
import com.portfolio.assetory.sellerorder.service.SellerOrderService;

@RestController
@RequestMapping("/api/seller/orders")
public class SellerOrderController {
	private final SellerOrderService service;
	public SellerOrderController(SellerOrderService service) { this.service = service; }

	@GetMapping
	public ResponseEntity<ApiResponse<SellerOrderListResponse>> list(@CurrentUserId Long sellerId, @RequestParam(required = false) OrderStatus status,
		@RequestParam(required = false) Long productId, @RequestParam(required = false) LocalDate startDate,
		@RequestParam(required = false) LocalDate endDate, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(ApiResponse.success(service.list(sellerId, status, productId, startDate, endDate, page, size)));
	}

	@GetMapping("/counts")
	public ResponseEntity<ApiResponse<SellerOrderCountsResponse>> counts(@CurrentUserId Long sellerId, @RequestParam(required = false) Long productId,
		@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate) {
		return ResponseEntity.ok(ApiResponse.success(service.counts(sellerId, productId, startDate, endDate)));
	}

	@GetMapping("/{orderItemId}")
	public ResponseEntity<ApiResponse<SellerOrderDetailResponse>> detail(@CurrentUserId Long sellerId, @PathVariable Long orderItemId) {
		return ResponseEntity.ok(ApiResponse.success(service.detail(sellerId, orderItemId)));
	}
}
