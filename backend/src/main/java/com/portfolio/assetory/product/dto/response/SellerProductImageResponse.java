package com.portfolio.assetory.product.dto.response;
import com.portfolio.assetory.product.domain.ProductImage;
import com.portfolio.assetory.product.domain.ProductImageType;
public record SellerProductImageResponse(Long id, String imageUrl, boolean isThumbnail) {
	public static SellerProductImageResponse from(ProductImage image) { return new SellerProductImageResponse(image.getId(), image.getImageUrl(), image.getImageType() == ProductImageType.THUMBNAIL); }
}
