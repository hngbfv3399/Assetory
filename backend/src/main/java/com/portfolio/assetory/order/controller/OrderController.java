package com.portfolio.assetory.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.order.dto.request.CreateCartOrderRequest;
import com.portfolio.assetory.order.dto.request.CreateDirectOrderRequest;
import com.portfolio.assetory.order.dto.response.OrderCreateResponse;
import com.portfolio.assetory.order.service.OrderService;
import com.portfolio.assetory.order.domain.OrderStatus;
import com.portfolio.assetory.order.dto.response.OrderListResponse;
import com.portfolio.assetory.order.dto.response.OrderDetailResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	private final OrderService orderService;
	public OrderController(OrderService orderService) { this.orderService = orderService; }
	@PostMapping
	public ResponseEntity<ApiResponse<OrderCreateResponse>> createFromCart(@CurrentUserId Long userId, @Valid @RequestBody CreateCartOrderRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(orderService.createFromCart(userId, request))); }
	@PostMapping("/direct")
	public ResponseEntity<ApiResponse<OrderCreateResponse>> createDirect(@CurrentUserId Long userId, @Valid @RequestBody CreateDirectOrderRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(orderService.createDirect(userId, request))); }
	@GetMapping public ResponseEntity<ApiResponse<OrderListResponse>> list(@CurrentUserId Long userId,@RequestParam(required=false) OrderStatus status,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size){return ResponseEntity.ok(ApiResponse.success(orderService.getOrders(userId,status,page,size)));}
	@GetMapping("/{orderId}") public ResponseEntity<ApiResponse<OrderDetailResponse>> detail(@CurrentUserId Long userId,@PathVariable Long orderId){return ResponseEntity.ok(ApiResponse.success(orderService.getOrder(userId,orderId)));}
}
