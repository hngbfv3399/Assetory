package com.portfolio.assetory.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.product.domain.ProductSort;
import com.portfolio.assetory.product.dto.response.ProductListResponse;
import com.portfolio.assetory.product.dto.response.ProductDetailResponse;
import com.portfolio.assetory.product.service.ProductQueryService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductQueryService productQueryService;

	public ProductController(ProductQueryService productQueryService) {
		this.productQueryService = productQueryService;
	}

	@GetMapping
	public ApiResponse<ProductListResponse> getProducts(
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) String keyword,
		@RequestParam(defaultValue = "LATEST") ProductSort sort,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	) {
		return ApiResponse.success(productQueryService.getPublicProducts(categoryId, keyword, sort, page, size));
	}

	@GetMapping("/{productId}")
	public ApiResponse<ProductDetailResponse> getProduct(@PathVariable Long productId) {
		return ApiResponse.success(productQueryService.getPublicProduct(productId));
	}
}
