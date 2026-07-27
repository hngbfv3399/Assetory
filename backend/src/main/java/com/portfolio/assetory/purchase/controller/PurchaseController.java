package com.portfolio.assetory.purchase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.purchase.dto.response.*;
import com.portfolio.assetory.purchase.service.PurchaseService;

@RestController @RequestMapping("/api/purchases")
public class PurchaseController {
	private final PurchaseService service;
	public PurchaseController(PurchaseService service) { this.service = service; }
	@GetMapping public ResponseEntity<ApiResponse<PurchaseListResponse>> list(@CurrentUserId Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) { return ResponseEntity.ok(ApiResponse.success(service.list(userId, page, size))); }
	@GetMapping("/{orderItemId}") public ResponseEntity<ApiResponse<PurchaseDetailResponse>> detail(@CurrentUserId Long userId, @PathVariable Long orderItemId) { return ResponseEntity.ok(ApiResponse.success(service.detail(userId, orderItemId))); }
	@GetMapping("/{orderItemId}/resources") public ResponseEntity<ApiResponse<PurchaseResourceResponse>> resources(@CurrentUserId Long userId, @PathVariable Long orderItemId) { return ResponseEntity.ok(ApiResponse.success(service.resources(userId, orderItemId))); }
	@GetMapping("/{orderItemId}/resources/{resourceId}/open") public ResponseEntity<ApiResponse<PurchaseLinkResponse>> open(@CurrentUserId Long userId, @PathVariable Long orderItemId, @PathVariable Long resourceId) { return ResponseEntity.ok(ApiResponse.success(service.open(userId, orderItemId, resourceId))); }
	@GetMapping("/{orderItemId}/resources/{resourceId}/download") public ResponseEntity<Void> download(@CurrentUserId Long userId, @PathVariable Long orderItemId, @PathVariable Long resourceId) { return ResponseEntity.status(org.springframework.http.HttpStatus.FOUND).location(java.net.URI.create(service.download(userId, orderItemId, resourceId))).build(); }
}
