package com.portfolio.assetory.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.product.dto.request.CreateSellerProductRequest;
import com.portfolio.assetory.product.dto.request.UpdateSellerProductRequest;
import com.portfolio.assetory.product.dto.request.CreateProductImageRequest;
import com.portfolio.assetory.product.dto.request.CreateProductResourceRequest;
import com.portfolio.assetory.product.dto.request.UpdateProductResourceRequest;
import com.portfolio.assetory.product.dto.response.SellerProductCreateResponse;
import com.portfolio.assetory.product.dto.response.SellerProductListResponse;
import com.portfolio.assetory.product.dto.response.SellerProductDetailResponse;
import com.portfolio.assetory.product.dto.response.SellerProductUpdateResponse;
import com.portfolio.assetory.product.dto.response.SellerProductImageResponse;
import com.portfolio.assetory.product.dto.response.SellerProductResourceResponse;
import com.portfolio.assetory.product.domain.ProductStatus;
import com.portfolio.assetory.product.service.SellerProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller/products")
public class SellerProductController {

	private final SellerProductService sellerProductService;

	public SellerProductController(SellerProductService sellerProductService) {
		this.sellerProductService = sellerProductService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<SellerProductListResponse>> getMyProducts(
		@CurrentUserId Long sellerId,
		@RequestParam(required = false) ProductStatus status,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	) {
		return ResponseEntity.ok(ApiResponse.success(sellerProductService.getMyProducts(sellerId, status, page, size)));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<SellerProductDetailResponse>> getMyProduct(
		@CurrentUserId Long sellerId,
		@PathVariable Long productId
	) {
		return ResponseEntity.ok(ApiResponse.success(sellerProductService.getMyProduct(sellerId, productId)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<SellerProductCreateResponse>> createProduct(
		@CurrentUserId Long sellerId,
		@Valid @RequestBody CreateSellerProductRequest request
	) {
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success(sellerProductService.createProduct(sellerId, request)));
	}

	@PatchMapping("/{productId}")
	public ResponseEntity<ApiResponse<SellerProductUpdateResponse>> updateProduct(@CurrentUserId Long sellerId, @PathVariable Long productId, @Valid @RequestBody UpdateSellerProductRequest request) {
		return ResponseEntity.ok(ApiResponse.success(sellerProductService.updateProduct(sellerId, productId, request)));
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(@CurrentUserId Long sellerId, @PathVariable Long productId) {
		sellerProductService.deleteProduct(sellerId, productId);
		return ResponseEntity.ok(ApiResponse.success("상품이 삭제되었습니다.", null));
	}

	@PostMapping("/{productId}/images")
	public ResponseEntity<ApiResponse<SellerProductImageResponse>> addImage(@CurrentUserId Long sellerId, @PathVariable Long productId, @Valid @RequestBody CreateProductImageRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(sellerProductService.addImage(sellerId, productId, request))); }

	@PostMapping("/{productId}/resources")
	public ResponseEntity<ApiResponse<SellerProductResourceResponse>> addResource(@CurrentUserId Long sellerId, @PathVariable Long productId, @Valid @RequestBody CreateProductResourceRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(sellerProductService.addResource(sellerId, productId, request))); }

	@DeleteMapping("/{productId}/images/{imageId}")
	public ResponseEntity<ApiResponse<Void>> deleteImage(@CurrentUserId Long sellerId, @PathVariable Long productId, @PathVariable Long imageId) { sellerProductService.deleteImage(sellerId, productId, imageId); return ResponseEntity.ok(ApiResponse.success("상품 이미지가 삭제되었습니다.", null)); }

	@PatchMapping("/{productId}/resources/{resourceId}")
	public ResponseEntity<ApiResponse<SellerProductResourceResponse>> updateResource(@CurrentUserId Long sellerId, @PathVariable Long productId, @PathVariable Long resourceId, @Valid @RequestBody UpdateProductResourceRequest request) { return ResponseEntity.ok(ApiResponse.success(sellerProductService.updateResource(sellerId, productId, resourceId, request))); }

	@DeleteMapping("/{productId}/resources/{resourceId}")
	public ResponseEntity<ApiResponse<Void>> deleteResource(@CurrentUserId Long sellerId, @PathVariable Long productId, @PathVariable Long resourceId) { sellerProductService.deleteResource(sellerId, productId, resourceId); return ResponseEntity.ok(ApiResponse.success("구매 자료가 삭제되었습니다.", null)); }

	@PatchMapping("/{productId}/publish")
	public ResponseEntity<ApiResponse<ProductStatus>> publish(@CurrentUserId Long sellerId, @PathVariable Long productId) { return ResponseEntity.ok(ApiResponse.success(sellerProductService.publishProduct(sellerId, productId))); }

	@PatchMapping("/{productId}/suspend")
	public ResponseEntity<ApiResponse<ProductStatus>> suspend(@CurrentUserId Long sellerId, @PathVariable Long productId) { return ResponseEntity.ok(ApiResponse.success(sellerProductService.suspendProduct(sellerId, productId))); }
}
