package com.portfolio.assetory.product.dto.response;
import com.portfolio.assetory.product.domain.ProductResource;
import com.portfolio.assetory.product.domain.ProductResourceType;
public record SellerProductResourceResponse(Long id, String name, ProductResourceType type) {
	public static SellerProductResourceResponse from(ProductResource resource) { return new SellerProductResourceResponse(resource.getId(), resource.getDisplayName(), resource.getResourceType()); }
}
